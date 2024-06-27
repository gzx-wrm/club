package com.gzx.club.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.domain.convert.SubjectCategoryConverter;
import com.gzx.club.subject.domain.convert.SubjectLabelConverter;
import com.gzx.club.subject.domain.entity.SubjectCategoryBO;
import com.gzx.club.subject.domain.entity.SubjectLabelBO;
import com.gzx.club.subject.domain.service.SubjectCategoryDomainService;
import com.gzx.club.subject.infra.basic.entity.SubjectCategory;
import com.gzx.club.subject.infra.basic.entity.SubjectLabel;
import com.gzx.club.subject.infra.basic.entity.SubjectMapping;
import com.gzx.club.subject.infra.basic.service.SubjectCategoryService;
import com.gzx.club.subject.infra.basic.service.SubjectLabelService;
import com.gzx.club.subject.infra.basic.service.SubjectMappingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubjectCategoryDomainServiceImpl implements SubjectCategoryDomainService {

    @Resource
    private SubjectCategoryService subjectCategoryService;

    @Resource
    private SubjectMappingService subjectMappingService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private SubjectLabelService subjectLabelService;

    public void add(SubjectCategoryBO subjectCategoryBO) {
        if (log.isInfoEnabled()) {
            log.info("SubjectCategoryController.add.bo:{}", JSON.toJSONString(subjectCategoryBO));
        }
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        subjectCategoryService.insert(subjectCategory);
    }

    public List<SubjectCategoryBO> queryCategory(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
        List<SubjectCategoryBO> boList = SubjectCategoryConverter.INSTANCE
                .convertCategoriesToBOs(subjectCategoryList);
        boList.forEach(bo -> {
            Integer subjectCount = subjectCategoryService.querySubjectCount(bo.getId());
            bo.setCount(subjectCount);
        });
        if (log.isInfoEnabled()) {
            log.info("SubjectCategoryController.queryPrimaryCategory.boList:{}",
                    JSON.toJSONString(boList));
        }
        return boList;
    }

    @Override
    public Boolean update(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        int count = subjectCategoryService.update(subjectCategory);
        return count > 0;
    }

    @Override
    public Boolean delete(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = SubjectCategoryConverter.INSTANCE
                .convertBoToCategory(subjectCategoryBO);
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        int count = subjectCategoryService.update(subjectCategory);
        return count > 0;
    }

    /**
     * @Author: gzx
     *
     * @Description: 查询大类下的分类以及对应的标签
     * @Date: 2024-06-26
     */
    @Override
    @SneakyThrows
    public List<SubjectCategoryBO> queryCategoryAndLabel(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = new SubjectCategory();
        subjectCategory.setParentId(subjectCategoryBO.getId());
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
        List<SubjectCategoryBO> subjectCategoryBOs = SubjectCategoryConverter.INSTANCE.convertCategoriesToBOs(subjectCategoryList);


        // 用线程池优化查询，注意要在所有线程都结束后才能够返回
        List<FutureTask<Map<Long, List<SubjectLabelBO>>>> futureTasks = new LinkedList<>();
//        subjectCategoryBOs.forEach(subjectCategoryBO1 -> {
//            FutureTask<Map<Long, List<SubjectLabelBO>>> futureTask = new FutureTask<>(() -> queryLabel(subjectCategoryBO1));
//            futureTasks.add(futureTask);
//            threadPoolExecutor.submit(futureTask);
//        });

        /* 使用CompletableFuture任务优化FutureTask，CompletableFuture是FutureTask的一种优化方案，
        提供了更加丰富的功能，比如支持链式调用、支持回调、非阻塞获取结果等
         */
        List<CompletableFuture<Map<Long, List<SubjectLabelBO>>>> futures = subjectCategoryBOs.stream()
                .map(subjectCategoryBO1 ->
                        CompletableFuture.supplyAsync(() ->
                                queryLabel(subjectCategoryBO), threadPoolExecutor))
                .collect(Collectors.toList());
        HashMap<Long, List<SubjectLabelBO>> map = new HashMap<>();
        for (CompletableFuture<Map<Long, List<SubjectLabelBO>>> future: futures) {
            // future get不到，会阻塞住
            Map<Long, List<SubjectLabelBO>> ret = future.get();
            if (CollectionUtils.isEmpty(ret)) {
                continue;
            }
            map.putAll(ret);
        }
        subjectCategoryBOs.forEach(subjectCategoryBO1 -> subjectCategoryBO1.setLabelBOList(map.get(subjectCategoryBO1.getId())));
        return subjectCategoryBOs;
    }

    private Map<Long, List<SubjectLabelBO>> queryLabel(SubjectCategoryBO subjectCategoryBO) {
        SubjectMapping subjectMapping = new SubjectMapping();
        subjectMapping.setCategoryId(subjectCategoryBO.getId());
        subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        List<SubjectMapping> subjectMappingList = subjectMappingService.queryDistinctLabelId(subjectMapping);
        if (subjectMappingList.size() <= 0) {
            return Collections.emptyMap();
        }
        List<Long> labelIds = subjectMappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
        List<SubjectLabel> subjectLabels = subjectLabelService.queryByIds(labelIds);
        List<SubjectLabelBO> labelBOList = new LinkedList<>();
        subjectLabels.forEach(label -> {
            SubjectLabelBO subjectLabelBO = new SubjectLabelBO();
            subjectLabelBO.setId(label.getId());
            subjectLabelBO.setLabelName(label.getLabelName());
            subjectLabelBO.setCategoryId(label.getCategoryId());
            subjectLabelBO.setSortNum(label.getSortNum());
            labelBOList.add(subjectLabelBO);
        });
        HashMap<Long, List<SubjectLabelBO>> ret = new HashMap<>();
        ret.put(subjectCategoryBO.getId(), labelBOList);
        return ret;
    }

}
