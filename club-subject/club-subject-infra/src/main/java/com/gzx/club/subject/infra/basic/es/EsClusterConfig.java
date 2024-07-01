package com.gzx.club.subject.infra.basic.es;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @program: club
 * @description: ES集群配置类
 * @author: gzx
 * @create: 2024-06-30
 **/
@Data
public class EsClusterConfig implements Serializable {

    /**
     * 集群名称
     */
    private String name;

    /**
     * 集群节点，以 , 分隔
     */
    private String nodes;
}
