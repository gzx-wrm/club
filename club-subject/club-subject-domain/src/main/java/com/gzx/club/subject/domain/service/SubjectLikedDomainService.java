package com.gzx.club.subject.domain.service;

import com.gzx.club.subject.common.entity.PageResult;
import com.gzx.club.subject.domain.entity.SubjectLikedBO;

/**
 * @program: club
 * @description: 题目点赞领域层服务
 * @author: gzx
 * @create: 2024-07-02
 **/
public interface SubjectLikedDomainService {
    void add(SubjectLikedBO subjectLikedBO);

    Boolean update(SubjectLikedBO subjectLikedBO);

    /**
     * 获取当前是否被点赞过
     */
    Boolean isLiked(String subjectId, String userId);

    /**
     * 获取点赞数量
     */
    Integer getLikedCount(String subjectId);

    Boolean delete(SubjectLikedBO subjectLikedBO);

    /**
     * 同步点赞数据
     */
    void syncLiked();

    void syncLikedByMsg(SubjectLikedBO subjectLikedBO);

    PageResult<SubjectLikedBO> getSubjectLikedPage(SubjectLikedBO subjectLikedBO);
}
