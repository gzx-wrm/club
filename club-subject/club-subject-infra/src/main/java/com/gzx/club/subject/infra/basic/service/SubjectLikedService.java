package com.gzx.club.subject.infra.basic.service;

import com.gzx.club.subject.infra.basic.entity.SubjectLiked;

import java.util.LinkedList;
import java.util.List;

/**
 * 题目点赞表(SubjectLiked)表服务接口
 *
 * @author makejava
 * @since 2024-07-02 13:24:45
 */
public interface SubjectLikedService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SubjectLiked queryById(Long id);


    /**
     * 新增数据
     *
     * @param subjectLiked 实例对象
     * @return 实例对象
     */
    SubjectLiked insert(SubjectLiked subjectLiked);

    /**
     * 修改数据
     *
     * @param subjectLiked 实例对象
     * @return 实例对象
     */
    Integer update(SubjectLiked subjectLiked);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    Integer insertBatch(LinkedList<SubjectLiked> likedLinkedList);

    Long countByCondition(SubjectLiked subjectLiked);

    List<SubjectLiked> queryPageByCondition(SubjectLiked subjectLiked, Integer offset, Integer limit);

    Integer insertOrUpdateBatch(LinkedList<SubjectLiked> likedLinkedList);
}
