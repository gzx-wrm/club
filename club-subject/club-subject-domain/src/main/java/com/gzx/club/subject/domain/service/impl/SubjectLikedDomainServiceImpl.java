package com.gzx.club.subject.domain.service.impl;

import com.alibaba.fastjson.JSON;
import com.gzx.club.subject.common.entity.PageResult;
import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.enums.SubjectLikedStatusEnum;
import com.gzx.club.subject.domain.convert.SubjectLikedConverter;
import com.gzx.club.subject.domain.entity.SubjectLikedBO;
import com.gzx.club.subject.domain.entity.SubjectLikedMessage;
import com.gzx.club.subject.domain.redis.RedisUtil;
import com.gzx.club.subject.domain.service.SubjectLikedDomainService;
import com.gzx.club.subject.infra.basic.entity.SubjectInfo;
import com.gzx.club.subject.infra.basic.entity.SubjectLiked;
import com.gzx.club.subject.infra.basic.service.SubjectInfoService;
import com.gzx.club.subject.infra.basic.service.SubjectLikedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: club
 * @description: 题目点赞服务实现类
 * @author: gzx
 * @create: 2024-07-02
 **/
@Service
@Slf4j
public class SubjectLikedDomainServiceImpl implements SubjectLikedDomainService {


    @Resource
    private RedisUtil redisUtil;

    @Resource
    private SubjectLikedService subjectLikedService;

    @Resource
    private SubjectInfoService subjectInfoService;

    @Resource
    private RocketMQTemplate mqTemplate;

    private static final String SUBJECT_LIKED_TOPIC = "subject-liked";

    // 这个key对应一个hash结构，用于暂存用户的收藏记录，这里的记录会被定时同步到数据库中
    private static final String SUBJECT_LIKED_KEY = "subject.liked";

    // 这个key对应String，用于记录题目的收藏数量
    private static final String SUBJECT_LIKED_COUNT_KEY = "subject.liked.count";

    // 这个key对应String，用于记录给定题目用户是否收藏
    private static final String SUBJECT_LIKED_DETAIL_KEY = "subject.liked.detail";


    @Override
    public void add(SubjectLikedBO subjectLikedBO) {
        String subjectIdString = String.valueOf(subjectLikedBO.getSubjectId());
        String userId = subjectLikedBO.getLikeUserId();
        Integer status = subjectLikedBO.getStatus();

        // 方案一：收藏操作放到redis中，使用定时任务刷到mysql中
//        String hashKey = buildSubjectLikedKey(subjectIdString, userId);
        String detailKey = redisUtil.buildKey(SUBJECT_LIKED_DETAIL_KEY, subjectIdString, userId);
//        redisUtil.hset(SUBJECT_LIKED_KEY, hashKey, status);

        // 方案二：收藏操作直接放到mq中，消费者监听到就能刷到mysql中
        SubjectLikedMessage subjectLikedMessage = new SubjectLikedMessage();
        subjectLikedMessage.setSubjectId(subjectLikedBO.getSubjectId());
        subjectLikedMessage.setLikeUserId(userId);
        subjectLikedMessage.setStatus(status);
        mqTemplate.convertAndSend("subject-liked", JSON.toJSONString(subjectLikedMessage));


        String countKey = redisUtil.buildKey(SUBJECT_LIKED_COUNT_KEY, subjectIdString);
        if (Objects.equals(status, SubjectLikedStatusEnum.LIKED.code)) {
            redisUtil.increBy(countKey, 1);
            redisUtil.set(detailKey, "1");
        }
        else if (Objects.equals(status, SubjectLikedStatusEnum.UN_LIKED.code)){
            Integer count = redisUtil.getInt(countKey);
            if (Objects.nonNull(count) && count > 0) {
                redisUtil.increBy(countKey, -1);
            }
            redisUtil.del(detailKey);
        }
    }

    @Override
    public Boolean update(SubjectLikedBO subjectLikedBO) {
        SubjectLiked subjectLiked = SubjectLikedConverter.INSTANCE.convertBOToEntity(subjectLikedBO);
        return subjectLikedService.update(subjectLiked) > 0;
    }

    @Override
    public Boolean isLiked(String subjectId, String userId) {
        String detailKey = redisUtil.buildKey(SUBJECT_LIKED_DETAIL_KEY, subjectId, userId);
        return redisUtil.exist(detailKey);
    }

    @Override
    public Integer getLikedCount(String subjectId) {
        String countKey = redisUtil.buildKey(SUBJECT_LIKED_COUNT_KEY, subjectId);
        Integer count = redisUtil.getInt(countKey);
        if (Objects.isNull(count) || count < 0) {
            return 0;
        }
        return count;
    }

    @Override
    public Boolean delete(SubjectLikedBO subjectLikedBO) {
        SubjectLiked subjectLiked = new SubjectLiked();
        subjectLiked.setId(subjectLikedBO.getId());
        subjectLiked.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        return subjectLikedService.update(subjectLiked) > 0;
    }

    @Override
    public void syncLiked() {
        Map<Object, Object> map = redisUtil.hgetAndDelete(SUBJECT_LIKED_KEY);
        if (log.isInfoEnabled()) {
            log.info("syncLiked.subjectLikedMap:{}", JSON.toJSONString(map));
        }
        LinkedList<SubjectLiked> likedLinkedList = new LinkedList<>();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Integer status = (Integer) entry.getValue();
            if (Objects.equals(SubjectLikedStatusEnum.UN_LIKED.code, status)) {
                continue;
            }
            SubjectLiked subjectLiked = new SubjectLiked();
            String subjectIdAndUserId = entry.getKey().toString();
            String[] split = subjectIdAndUserId.split(":");

            subjectLiked.setLikeUserId(split[1]);
            subjectLiked.setSubjectId(Long.valueOf(split[0]));
            subjectLiked.setStatus(SubjectLikedStatusEnum.LIKED.code);
            subjectLiked.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
            likedLinkedList.add(subjectLiked);
        }
        subjectLikedService.insertOrUpdateBatch(likedLinkedList);
    }

    @Override
    public void syncLikedByMsg(SubjectLikedBO subjectLikedBO) {
        SubjectLiked subjectLiked = new SubjectLiked();
        subjectLiked.setSubjectId(subjectLikedBO.getSubjectId());
        subjectLiked.setLikeUserId(subjectLikedBO.getLikeUserId());
        subjectLiked.setStatus(subjectLikedBO.getStatus());
        subjectLiked.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());

        subjectLikedService.insert(subjectLiked);
    }

    @Override
    public PageResult<SubjectLikedBO> getSubjectLikedPage(SubjectLikedBO subjectLikedBO) {
        PageResult<SubjectLikedBO> pageResult = new PageResult<>();
        pageResult.setPageNo(subjectLikedBO.pageNo());
        pageResult.setPageSize(subjectLikedBO.pageSize());

        subjectLikedBO.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
        subjectLikedBO.setStatus(SubjectLikedStatusEnum.LIKED.code);
        SubjectLiked subjectLiked = SubjectLikedConverter.INSTANCE.convertBOToEntity(subjectLikedBO);
        long count = subjectLikedService.countByCondition(subjectLiked);
        if (count <= 0) {
            return pageResult;
        }
        Integer offset = (pageResult.getPageNo() - 1) * pageResult.getPageSize();
        List<SubjectLiked> subjectLikedList = subjectLikedService.queryPageByCondition(subjectLiked, offset, pageResult.getPageSize());
        List<SubjectLikedBO> subjectLikedBOS = SubjectLikedConverter.INSTANCE.convertEntityToBO(subjectLikedList);
        subjectLikedBOS.forEach(info -> {
            SubjectInfo subjectInfo = subjectInfoService.queryById(info.getSubjectId());
            info.setSubjectName(subjectInfo.getSubjectName());
        });
        pageResult.setRecords(subjectLikedBOS);

        return pageResult;
    }

    private String buildSubjectLikedKey(String subjectId, String userId) {
        return subjectId + ":" + userId;
    }
}
