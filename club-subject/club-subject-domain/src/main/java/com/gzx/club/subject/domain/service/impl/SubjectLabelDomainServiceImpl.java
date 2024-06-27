package com.gzx.club.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzx.club.subject.common.enums.CategoryTypeEnum;
import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.domain.convert.SubjectLabelConverter;
import com.gzx.club.subject.domain.entity.SubjectLabelBO;
import com.gzx.club.subject.domain.service.SubjectLabelDomainService;
import com.gzx.club.subject.infra.basic.entity.SubjectCategory;
import com.gzx.club.subject.infra.basic.entity.SubjectLabel;
import com.gzx.club.subject.infra.basic.entity.SubjectMapping;
import com.gzx.club.subject.infra.basic.service.SubjectCategoryService;
import com.gzx.club.subject.infra.basic.service.SubjectLabelService;
import com.gzx.club.subject.infra.basic.service.SubjectMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubjectLabelDomainServiceImpl implements SubjectLabelDomainService {

    @Resource
    private SubjectLabelService subjectLabelService;

    @Resource
    private SubjectCategoryService subjectCategoryService;

    @Resource
    private SubjectMappingService subjectMappingService;

    @Override
    public Boolean add(SubjectLabelBO subjectLabelBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectLabelDomainServiceImpl.add.bo:{}", JSON.toJSONString(subjectLabelBO));
        }
        SubjectLabel subjectLabel = SubjectLabelConverter.INSTANCE
                .convertBoToLabel(subjectLabelBO);
        subjectLabel.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        int count = subjectLabelService.insert(subjectLabel);
        return count > 0;
    }

    @Override
    public Boolean update(SubjectLabelBO subjectLabelBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectLabelDomainServiceImpl.update.bo:{}", JSON.toJSONString(subjectLabelBO));
        }
        SubjectLabel subjectLabel = SubjectLabelConverter.INSTANCE
                .convertBoToLabel(subjectLabelBO);
        int count = subjectLabelService.update(subjectLabel);
        return count > 0;
    }

    @Override
    public Boolean delete(SubjectLabelBO subjectLabelBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectLabelDomainServiceImpl.update.bo:{}", JSON.toJSONString(subjectLabelBO));
        }
        SubjectLabel subjectLabel = SubjectLabelConverter.INSTANCE
                .convertBoToLabel(subjectLabelBO);
        subjectLabel.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        int count = subjectLabelService.update(subjectLabel);
        return count > 0;
    }

    @Override
    public List<SubjectLabelBO> queryLabelByCategoryId(SubjectLabelBO subjectLabelBO) {
        /*
        这里就体现了domain组装基础设施层，使其能完成该领域的各个任务
         */
        //如果当前分类是1级分类，则查询所有标签
        SubjectCategory subjectCategory = subjectCategoryService.queryById(subjectLabelBO.getCategoryId());
        if(CategoryTypeEnum.PRIMARY.getCode() == subjectCategory.getCategoryType()){
            SubjectLabel subjectLabel = new SubjectLabel();
            subjectLabel.setCategoryId(subjectLabelBO.getCategoryId());
            List<SubjectLabel> labelList = subjectLabelService.queryByCondition(subjectLabel);
            return SubjectLabelConverter.INSTANCE.convertLabelToBoList(labelList);
        }
        Long categoryId = subjectLabelBO.getCategoryId();
        SubjectMapping subjectMapping = new SubjectMapping();
        subjectMapping.setCategoryId(categoryId);
        subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectMapping> mappingList = subjectMappingService.queryDistinctLabelId(subjectMapping);
        if (CollectionUtils.isEmpty(mappingList)) {
            return Collections.emptyList();
        }
        List<Long> labelIdList = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
        List<SubjectLabel> labelList = subjectLabelService.queryByIds(labelIdList);
        List<SubjectLabelBO> boList = SubjectLabelConverter.INSTANCE.convertLabelToBoList(labelList);
//        labelList.forEach(label -> {
//            SubjectLabelBO bo = new SubjectLabelBO();
//            bo.setId(label.getId());
//            bo.setLabelName(label.getLabelName());
//            bo.setCategoryId(categoryId);
//            bo.setSortNum(label.getSortNum());
//            boList.add(bo);
//        });
        return boList;
    }
}
