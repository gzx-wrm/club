package com.gzx.club.subject.infra.basic.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: club
 * @description: ES配置类
 * @author: gzx
 * @create: 2024-06-30
 **/
@Component
@ConfigurationProperties(prefix = "es.cluster")
@Data
public class EsConfigProperties {
    // 存储若干个es集群配置
    private List<EsClusterConfig> esConfigs = new ArrayList<>();
}
