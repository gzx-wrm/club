package com.gzx.club.subject.infra.basic.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 题目点赞表(SubjectLiked)实体类
 *
 * @author makejava
 * @since 2024-06-03 15:07:19
 */
public class SubjectLiked implements Serializable {
    private static final long serialVersionUID = -31853841377159297L;
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(String likeUserId) {
        this.likeUserId = likeUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

}

