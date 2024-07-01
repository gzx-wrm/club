package com.gzx.club.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzx.club.subject.common.entity.PageResult;
import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.utils.IdWorkerUtil;
import com.gzx.club.subject.common.utils.LoginUtil;
import com.gzx.club.subject.domain.convert.SubjectInfoConverter;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.domain.handler.SubjectTypeHandler;
import com.gzx.club.subject.domain.handler.SubjectTypeHandlerFactory;
import com.gzx.club.subject.domain.redis.RedisUtil;
import com.gzx.club.subject.domain.service.SubjectInfoDomainService;
import com.gzx.club.subject.infra.basic.entity.EsSubjectInfo;
import com.gzx.club.subject.infra.basic.entity.SubjectInfo;
import com.gzx.club.subject.infra.basic.entity.SubjectLabel;
import com.gzx.club.subject.infra.basic.entity.SubjectMapping;
import com.gzx.club.subject.infra.basic.service.SubjectEsService;
import com.gzx.club.subject.infra.basic.service.SubjectInfoService;
import com.gzx.club.subject.infra.basic.service.SubjectLabelService;
import com.gzx.club.subject.infra.basic.service.SubjectMappingService;
import com.gzx.club.subject.infra.entity.UserInfo;
import com.gzx.club.subject.infra.rpc.UserRPC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: club
 * @description: 题目service实现类
 * @author: gzx
 * @create: 2024-06-17 15:32
 **/
@Service
@Slf4j
public class SubjectInfoDomainServiceImpl implements SubjectInfoDomainService {

    @Resource
    SubjectInfoService subjectInfoService;

    @Resource
    SubjectTypeHandlerFactory subjectTypeHandlerFactory;

    @Resource
    SubjectMappingService subjectMappingService;

    @Resource
    SubjectEsService subjectEsService;

    @Resource
    SubjectLabelService subjectLabelService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserRPC userRPC;

    private static final String RANK_KEY = "subject_rank";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SubjectInfoBO subjectInfoBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectInfoDomainServiceImpl.add.bo:{}", JSON.toJSONString(subjectInfoBO));
        }
        // 先插入题目表
        SubjectInfo subjectInfo = SubjectInfoConverter.INSTANCE.convertBoToInfo(subjectInfoBO);
        subjectInfo.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        subjectInfoService.insert(subjectInfo);
        subjectInfoBO.setId(subjectInfo.getId());

        // 然后使用工厂插入
        SubjectTypeHandler handler = subjectTypeHandlerFactory.getHandler(subjectInfoBO.getSubjectType());
        handler.add(subjectInfoBO);

        // 插入到category-label-subject映射表中
        List<Integer> categoryIds = subjectInfoBO.getCategoryIds();
        List<Integer> labelIds = subjectInfoBO.getLabelIds();
        List<SubjectMapping> mappingList = new LinkedList<>();
        categoryIds.forEach(categoryId -> {
            labelIds.forEach(labelId -> {
                SubjectMapping subjectMapping = new SubjectMapping();
                subjectMapping.setSubjectId(subjectInfo.getId());
                subjectMapping.setCategoryId(Long.valueOf(categoryId));
                subjectMapping.setLabelId(Long.valueOf(labelId));
                subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
                mappingList.add(subjectMapping);
            });
        });
        subjectMappingService.insertBatch(mappingList);

        //同步到es
        EsSubjectInfo subjectInfoEs = new EsSubjectInfo();
        subjectInfoEs.setDocId(new IdWorkerUtil(1, 1, 1).nextId());
        subjectInfoEs.setSubjectId(subjectInfo.getId());
        subjectInfoEs.setSubjectAnswer(subjectInfoBO.getSubjectAnswer());
        subjectInfoEs.setCreateTime(new Date().getTime());
        subjectInfoEs.setCreateUser("鸡翅");
        subjectInfoEs.setSubjectName(subjectInfo.getSubjectName());
        subjectInfoEs.setSubjectType(subjectInfo.getSubjectType());
        subjectEsService.insert(subjectInfoEs);

        // 同步到redis
        redisUtil.addScore(RANK_KEY, LoginUtil.getLoginId(), 1);
    }

    @Override
    public PageResult<SubjectInfoBO> getSubjectPage(SubjectInfoBO subjectInfoBO) {
        PageResult<SubjectInfoBO> pageResult = new PageResult<>();
        pageResult.setPageNo(subjectInfoBO.pageNo());
        pageResult.setPageSize(subjectInfoBO.pageSize());

        SubjectInfo subjectInfo = SubjectInfoConverter.INSTANCE.convertBoToInfo(subjectInfoBO);
        long count = subjectInfoService.countByCondition(subjectInfo, subjectInfoBO.getCategoryId(), subjectInfoBO.getLabelId());
        if (count <= 0) {
            return pageResult;
        }

        Integer offset = (pageResult.getPageNo() - 1) * pageResult.getPageSize();

        List<SubjectInfo> subjectInfoList = subjectInfoService.queryPage(subjectInfo, subjectInfoBO.getCategoryId(), subjectInfoBO.getLabelId(), offset, subjectInfoBO.pageSize());
        pageResult.setTotal(subjectInfoList.size());
        pageResult.setRecords(SubjectInfoConverter.INSTANCE.convertInfosToBos(subjectInfoList));

        return pageResult;
    }

    @Override
    public SubjectInfoBO querySubjectInfo(SubjectInfoBO subjectInfoBO) {
        SubjectInfo subjectInfo = subjectInfoService.queryById(subjectInfoBO.getId());

        SubjectTypeHandler handler = subjectTypeHandlerFactory.getHandler(subjectInfo.getSubjectType());
        SubjectOptionBO optionBO = handler.query(subjectInfo.getId());

        SubjectInfoBO subjectInfoBO1 = SubjectInfoConverter.INSTANCE.convertOptionAndInfoToBo(optionBO, subjectInfo);

        SubjectMapping subjectMapping = new SubjectMapping();
        subjectMapping.setSubjectId(subjectInfo.getId());
        subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectMapping> mappingList = subjectMappingService.queryDistinctLabelId(subjectMapping);
        List<Long> labelIdList = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
        List<SubjectLabel> labelList = subjectLabelService.queryByIds(labelIdList);
        List<String> labelNameList = labelList.stream().map(SubjectLabel::getLabelName).collect(Collectors.toList());
        subjectInfoBO1.setLabelName(labelNameList);

        return subjectInfoBO1;
    }

    @Override
    public PageResult<EsSubjectInfo> getSubjectPageBySearch(SubjectInfoBO subjectInfoBO) {
        PageResult<EsSubjectInfo> pageResult = new PageResult<>();
        Integer pageNo = subjectInfoBO.pageNo();
        Integer pageSize = subjectInfoBO.pageSize();
        pageResult.setPageNo(pageNo);
        pageResult.setPageSize(pageSize);

        EsSubjectInfo subjectInfoEs = new EsSubjectInfo();
        subjectInfoEs.setKeyword(subjectInfoBO.getKeyword());
        int from = (pageNo - 1) * pageSize;
        List<EsSubjectInfo> esSubjectInfos = subjectEsService.querySubjectList(subjectInfoEs, from, pageSize);
        pageResult.setRecords(esSubjectInfos);

        return pageResult;
    }

    @Override
    public List<SubjectInfoBO> getContributeList() {
        Set<ZSetOperations.TypedTuple<String>> typedTuples = redisUtil.rangeWithScore(RANK_KEY, 0, 5);
        LinkedList<SubjectInfoBO> res = new LinkedList<>();
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            SubjectInfoBO subjectInfoBO = new SubjectInfoBO();
            subjectInfoBO.setSubjectCount(typedTuple.getScore().intValue());
            UserInfo userInfo = userRPC.getUserInfo(typedTuple.getValue());
            subjectInfoBO.setCreateUser(userInfo.getNickName());
            subjectInfoBO.setCreateUserAvatar(userInfo.getAvatar());

            res.add(subjectInfoBO);
        }
        return res;
    }
}
