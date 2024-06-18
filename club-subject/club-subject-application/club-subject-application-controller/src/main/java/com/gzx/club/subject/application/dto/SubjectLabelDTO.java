package com.gzx.club.subject.application.dto;

import lombok.Data;

/**
 * @program: club
 * @description:
 * @author: gzx
 * @create: 2024-06-03 23:43
 **/
@Data
public class SubjectLabelDTO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 标签分类
     */
    private String labelName;
    /**
     * 排序
     */
    private Integer sortNum;
}
