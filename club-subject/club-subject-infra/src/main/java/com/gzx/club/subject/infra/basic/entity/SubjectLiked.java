package com.gzx.club.subject.infra.basic.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 题目点赞表(SubjectLiked)实体类
 *
 * @author makejava
 * @since 2024-07-02 13:24:06
 */
@Data
public class SubjectLiked implements Serializable {
    private static final long serialVersionUID = -29136865392637249L;
    /**
     * 主键
     */
    private Long id;
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
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;

    private Integer isDeleted;

}

