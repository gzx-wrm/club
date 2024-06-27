package com.gzx.club.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubjectCategoryDomainServiceImpl implements SubjectCategoryDomainService {

    @Resource
    private SubjectCategoryService subjectCategoryService;

    @Resource
    private SubjectMappingService subjectMappingService;

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

//    @Override
//    public List<SubjectCategoryBO> queryCategoryAndLabel(SubjectCategoryBO subjectCategoryBO) {
//        //查询当前大类下所有分类
//        SubjectCategory subjectCategory = new SubjectCategory();
//        subjectCategory.setParentId(subjectCategoryBO.getId());
//        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
//        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
//        if (log.isInfoEnabled()) {
//            log.info("SubjectCategoryController.queryCategoryAndLabel.subjectCategoryList:{}",
//                    JSON.toJSONString(subjectCategoryList));
//        }
//        List<SubjectCategoryBO> categoryBOList = SubjectCategoryConverter.INSTANCE.convertBoToCategory(subjectCategoryList);
//        //一次获取标签信息
//        categoryBOList.forEach(category -> {
//            SubjectMapping subjectMapping = new SubjectMapping();
//            subjectMapping.setCategoryId(category.getId());
//            List<SubjectMapping> mappingList = subjectMappingService.queryLabelId(subjectMapping);
//            if (CollectionUtils.isEmpty(mappingList)) {
//                return;
//            }
//            List<Long> labelIdList = mappingList.stream().map(SubjectMapping::getLabelId).collect(Collectors.toList());
//            List<SubjectLabel> labelList = subjectLabelService.batchQueryById(labelIdList);
//            List<SubjectLabelBO> labelBOList = new LinkedList<>();
//            labelList.forEach(label -> {
//                SubjectLabelBO subjectLabelBO = new SubjectLabelBO();
//                subjectLabelBO.setId(label.getId());
//                subjectLabelBO.setLabelName(label.getLabelName());
//                subjectLabelBO.setCategoryId(label.getCategoryId());
//                subjectLabelBO.setSortNum(label.getSortNum());
//                labelBOList.add(subjectLabelBO);
//            });
//            category.setLabelBOList(labelBOList);
//        });
//        return categoryBOList;
//    }

    /**
     * @Author: gzx
     *
     * @Description: 查询大类下的分类以及对应的标签
     * @Date: 2024-06-26
     */
    @Override
    public List<SubjectCategoryBO> queryCategoryAndLabel(SubjectCategoryBO subjectCategoryBO) {
        SubjectCategory subjectCategory = new SubjectCategory();
        subjectCategory.setParentId(subjectCategoryBO.getId());
        subjectCategory.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        List<SubjectCategory> subjectCategoryList = subjectCategoryService.queryCategory(subjectCategory);
        List<SubjectCategoryBO> subjectCategoryBOs = SubjectCategoryConverter.INSTANCE.convertCategoriesToBOs(subjectCategoryList);

        subjectCategoryBOs.forEach(subjectCategoryBO1 -> {
            SubjectMapping subjectMapping = new SubjectMapping();
            subjectMapping.setCategoryId(subjectCategoryBO1.getId());
            subjectMapping.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
            List<SubjectMapping> subjectMappingList = subjectMappingService.queryDistinctLabelId(subjectMapping);
            if (subjectMappingList.size() <= 0) {
                return;
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
            subjectCategoryBO1.setLabelBOList(labelBOList);
        });

        return subjectCategoryBOs;
    }

}
