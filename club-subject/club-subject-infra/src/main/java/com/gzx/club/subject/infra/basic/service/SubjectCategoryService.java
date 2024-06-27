package com.gzx.club.subject.infra.basic.service;

import com.gzx.club.subject.infra.basic.entity.SubjectCategory;

import java.util.List;

/**
 * 题目分类(SubjectCategory)表服务接口
 *
 * @author makejava
 * @since 2024-06-03 15:06:25
 */
public interface SubjectCategoryService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SubjectCategory queryById(Long id);

    List<SubjectCategory> queryCategory(SubjectCategory subjectCategory);


    /**
     * 新增数据
     *
     * @param subjectCategory 实例对象
     * @return 实例对象
     */
    SubjectCategory insert(SubjectCategory subjectCategory);

    /**
     * 修改数据
     *
     * @param subjectCategory 实例对象
     * @return 实例对象
     */
    int update(SubjectCategory subjectCategory);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    int deleteById(Long id);

    Integer querySubjectCount(Long id);


    /**
     * @Author: gzx
     *
     * @Description: 联表查询分类和标签
     * @Date: 2024-06-25
     */
    List<SubjectCategory> queryCategoryAndLabel(SubjectCategory subjectCategory);
}
