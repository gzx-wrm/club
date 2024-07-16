package com.gzx.club.practice.api.entity;

import lombok.Data;

/**
 * 分页请求实体
 *
 * @author: ChickenWing
 * @date: 2023/10/5
 */
@Data
public class PageInfo {

    private Integer pageNo;

    private Integer pageSize;

    /*
    这里记录一下，pageInfo类被BO继承，如果想要在一些时候不序列化pageNo和pageSize字段，则值应该为null
    而getPageNo方法无论如何都会在被序列化时调用，会返回一个默认值很难看
    所以这两个get方法不能以get为开头，否则会被序列化方法调用
     */
    public Integer pageNo() {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        return pageNo;
    }

    public Integer pageSize() {
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        return pageSize;
    }
}
