package com.gzx.club.subject.application.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 题目答案dto
 *
 * @author: ChickenWing
 * @date: 2023/10/5
 */
@Data
public class SubjectAnswerDTO implements Serializable {

    /**
     * 答案选项标识
     */
    private Integer optionType;

    /**
     * 答案
     */
    private String optionContent;

    /**
     * 是否正确
     */
    private Integer isCorrect;

}

