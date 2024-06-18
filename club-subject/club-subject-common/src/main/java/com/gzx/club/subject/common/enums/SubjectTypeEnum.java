package com.gzx.club.subject.common.enums;

public enum SubjectTypeEnum {

    RADIO(1, "单选"),
    MULTIPLE(2, "多选"),
    JUDGE(3, "判断"),
    BRIEF(4, "简答");

    public Integer code;

    public String desc;

    SubjectTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SubjectTypeEnum getByCode(int codeVal) {
        for (SubjectTypeEnum subjectTypeEnum : SubjectTypeEnum.values()) {
            if (subjectTypeEnum.code == codeVal) {
                return subjectTypeEnum;
            }
        }
        return null;
    }
}
