package com.gzx.club.subject.common.enums;

import lombok.Getter;

/**
 * @program: club
 * @description: 响应状态码枚举类
 * @author: gzx
 * @create: 2024-06-03 16:20
 **/
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(500, "失败");

    public Integer code;

    public String desc;

    ResultCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResultCodeEnum getByCode(int codeVal){
        for(ResultCodeEnum resultCodeEnum : ResultCodeEnum.values()){
            if(resultCodeEnum.code == codeVal){
                return resultCodeEnum;
            }
        }
        return null;
    }
}
