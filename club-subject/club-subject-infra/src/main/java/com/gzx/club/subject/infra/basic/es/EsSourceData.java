package com.gzx.club.subject.infra.basic.es;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: club
 * @description: ES搜索结果封装类
 * @author: gzx
 * @create: 2024-06-30
 **/
@Data
public class EsSourceData implements Serializable {

    // 文档的id，和数据库实体的id不同，是es内的唯一id
    private String docId;

    // 文档内容，是以json格式返回的，所以用map来接收，后面根据需要封装到类中
    private Map<String, Object> data;
}
