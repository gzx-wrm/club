package com.gzx.club.subject.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目标签bo
 *
 * @author: ChickenWing
 * @date: 2023/10/3
 */
@Data
public class SubjectLabelBO implements Serializable {

    private Long id;
    /**
     * 标签分类
     */
    private String labelName;
    /**
     * 排序
     */
    private Integer sortNum;

    private Long categoryId;

}

