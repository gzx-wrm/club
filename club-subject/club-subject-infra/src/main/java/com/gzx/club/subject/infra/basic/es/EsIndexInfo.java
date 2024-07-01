package com.gzx.club.subject.infra.basic.es;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: club
 * @description: 封装es索引信息
 * @author: gzx
 * @create: 2024-06-30
 **/
@Data
public class EsIndexInfo implements Serializable {
    /**
     * 集群名称
     */
    private String clusterName;

    /**
     * 索引名称
     */
    private String indexName;
}
