package com.gzx.club.practice.server.service.impl;

import com.gzx.club.practice.api.enums.SubjectInfoTypeEnum;
import com.gzx.club.practice.api.vo.SpecialPracticeCategoryVO;
import com.gzx.club.practice.api.vo.SpecialPracticeLabelVO;
import com.gzx.club.practice.api.vo.SpecialPracticeVO;
import com.gzx.club.practice.server.entity.dto.CategoryDTO;
import com.gzx.club.practice.server.entity.po.CategoryPO;
import com.gzx.club.practice.server.entity.po.LabelCountPO;
import com.gzx.club.practice.server.entity.po.PrimaryCategoryPO;
import com.gzx.club.practice.server.entity.po.SubjectLabelPO;
import com.gzx.club.practice.server.mapper.SubjectCategoryDao;
import com.gzx.club.practice.server.mapper.SubjectLabelDao;
import com.gzx.club.practice.server.mapper.SubjectMappingDao;
import com.gzx.club.practice.server.service.PracticeSetService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: club
 * @description: 习题册服务实现类
 * @author: gzx
 * @create: 2024-07-07
 **/
@Service
public class PracticeSetServiceImpl implements PracticeSetService {


    @Resource
    private SubjectCategoryDao subjectCategoryDao;

    @Resource
    private SubjectMappingDao subjectMappingDao;

    @Resource
    private SubjectLabelDao subjectLabelDao;

    @Override
    public List<SpecialPracticeVO> getSpecialPracticeContent() {
        LinkedList<SpecialPracticeVO> ret = new LinkedList<>();

        LinkedList<Integer> subjectTypes = new LinkedList<>();
        subjectTypes.add(SubjectInfoTypeEnum.RADIO.code);
        subjectTypes.add(SubjectInfoTypeEnum.MULTIPLE.code);
        subjectTypes.add(SubjectInfoTypeEnum.JUDGE.code);

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setSubjectTypeList(subjectTypes);
        List<PrimaryCategoryPO> primaryCategoryPOList = subjectCategoryDao.getPrimaryCategory(categoryDTO);
        if (CollectionUtils.isEmpty(primaryCategoryPOList)) {
            return ret;
        }

        primaryCategoryPOList.forEach(primaryCategoryPO -> {
            SpecialPracticeVO specialPracticeVO = new SpecialPracticeVO();
            specialPracticeVO.setPrimaryCategoryId(primaryCategoryPO.getId());
            PrimaryCategoryPO categoryPO = subjectCategoryDao.queryById(primaryCategoryPO.getId());
            specialPracticeVO.setPrimaryCategoryName(categoryPO.getCategoryName());

            CategoryDTO categoryDTO1 = new CategoryDTO();
            categoryDTO1.setParentId(primaryCategoryPO.getId());
            categoryDTO1.setCategoryType(2);
            List<CategoryPO> categoryPOList = subjectCategoryDao.queryByCondition(categoryDTO1);
            LinkedList<SpecialPracticeCategoryVO> categoryVOLinkedList = new LinkedList<>();
            categoryPOList.forEach(categoryPO1 -> {
                SpecialPracticeCategoryVO specialPracticeCategoryVO = new SpecialPracticeCategoryVO();
                specialPracticeCategoryVO.setCategoryId(categoryPO1.getId());
                specialPracticeCategoryVO.setCategoryName(categoryPO1.getCategoryName());

                List<SpecialPracticeLabelVO> labelVOList = getLabelVOList(categoryPO1.getId(), subjectTypes);
                if (CollectionUtils.isEmpty(labelVOList)) {
                    return;
                }
                specialPracticeCategoryVO.setLabelList(labelVOList);
                categoryVOLinkedList.add(specialPracticeCategoryVO);
            });
            specialPracticeVO.setCategoryList(categoryVOLinkedList);
            ret.add(specialPracticeVO);
        });
        return ret;
    }

    private List<SpecialPracticeLabelVO> getLabelVOList(Long categoryId, List<Integer> subjectTypeList) {
        List<LabelCountPO> countPOList = subjectMappingDao.getLabelSubjectCount(categoryId, subjectTypeList);
        if(CollectionUtils.isEmpty(countPOList)){
            return Collections.emptyList();
        }
        List<SpecialPracticeLabelVO> voList = new LinkedList<>();
        countPOList.forEach(countPo->{
            SpecialPracticeLabelVO vo = new SpecialPracticeLabelVO();
            vo.setId(countPo.getLabelId());
            vo.setAssembleId(categoryId + "-" + countPo.getLabelId());
            SubjectLabelPO subjectLabelPO = subjectLabelDao.queryById(countPo.getLabelId());
            vo.setLabelName(subjectLabelPO.getLabelName());
            voList.add(vo);
        });
        return voList;
    }
}
