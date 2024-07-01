package com.gzx.club.subject.domain.entity;

import com.gzx.club.subject.common.entity.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SubjectInfoBO extends PageInfo implements Serializable {

    /**
     * 主键
     */
    private Long id;

    private Long categoryId;

    private Long labelId;
    /**
     * 题目名称
     */
    private String subjectName;
    /**
     * 题目难度
     */
    private Integer subjectDifficult;
    /**
     * 出题人名
     */
    private String settleName;
    /**
     * 题目类型 1单选 2多选 3判断 4简答
     */
    private Integer subjectType;
    /**
     * 题目分数
     */
    private Integer subjectScore;
    /**
     * 题目解析
     */
    private String subjectParse;

    /**
     * 题目答案
     */
    private String subjectAnswer;

    /**
     * 分类id
     */
    private List<Integer> categoryIds;

    /**
     * 标签id
     */
    private List<Integer> labelIds;

    /**
     * 答案选项
     */
    private List<SubjectAnswerBO> optionList;

    /**
     * 标签name
     */
    private List<String> labelName;

    /**
     * 用于全文搜索的关键字
     */
    private String keyword;

    /**
     * 创建人昵称，获取贡献排行榜时用
     */
    private String createUser;

    /**
     * 创建人头像，获取贡献排行榜时用
     */
    private String createUserAvatar;

    /**
     * 题目数量，获取贡献排行榜时用
     */
    private Integer subjectCount;

}

