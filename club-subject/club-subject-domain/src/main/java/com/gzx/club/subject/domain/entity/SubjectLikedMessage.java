package com.gzx.club.subject.domain.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: club
 * @description: 收藏消息在mq中的实体
 * @author: gzx
 * @create: 2024-07-12
 **/
@Data
public class SubjectLikedMessage implements Serializable {


    /**
     * 题目id
     */
    private Long subjectId;

    /**
     * 点赞人id
     */
    private String likeUserId;

    /**
     * 点赞状态 1点赞 0不点赞
     */
    private Integer status;

}
