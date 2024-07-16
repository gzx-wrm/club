package com.gzx.club.practice.server.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: gzx
 *
 * @Description: 用于dao层操作的入参
 * @Date: 2024-07-07
 */
@Data
public class CategoryDTO {

    private List<Integer> subjectTypeList;

    private Integer categoryType;

    private Long parentId;

}
