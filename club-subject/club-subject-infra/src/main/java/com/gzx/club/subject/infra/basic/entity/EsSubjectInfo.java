package com.gzx.club.subject.infra.basic.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: club
 * @description: ES题目类
 * @author: gzx
 * @create: 2024-06-30
 **/
@Data
public class EsSubjectInfo {

    private Long subjectId;

    private Long docId;

    private String subjectName;

    private String subjectAnswer;

    private String createUser;

    private Long createTime;

    private Integer subjectType;

    /**
     * 关键字，关键字在搜索的时候是不分词的
     */
    private String keyword;

    private BigDecimal score;
}
