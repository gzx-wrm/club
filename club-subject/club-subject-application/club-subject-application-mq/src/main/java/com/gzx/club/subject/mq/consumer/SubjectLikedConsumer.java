package com.gzx.club.subject.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.gzx.club.subject.domain.entity.SubjectLikedBO;
import com.gzx.club.subject.domain.entity.SubjectLikedMessage;
import com.gzx.club.subject.domain.service.SubjectLikedDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: club
 * @description: 同步刷新收藏数据
 * @author: gzx
 * @create: 2024-07-12
 **/
@Slf4j
@RocketMQMessageListener(consumerGroup = "subject-liked-consumer", topic = "subject-liked")
@Component
public class SubjectLikedConsumer implements RocketMQListener<String> {


    @Resource
    private SubjectLikedDomainService subjectLikedDomainService;

    @Override
    public void onMessage(String s) {
        log.info("收到subject-liked消息, {}", s);
        subjectLikedDomainService.syncLikedByMsg(JSON.parseObject(s, SubjectLikedBO.class));
    }
}
