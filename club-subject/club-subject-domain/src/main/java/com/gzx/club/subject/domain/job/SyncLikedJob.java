package com.gzx.club.subject.domain.job;

import com.gzx.club.subject.domain.service.SubjectLikedDomainService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: club
 * @description: 定时任务
 * @author: gzx
 * @create: 2024-07-02
 **/
@Component
@Slf4j
public class SyncLikedJob {

    @Resource
    SubjectLikedDomainService subjectLikedDomainService;

    /**
     * @Author: gzx
     *
     * @Description: 同步点赞定时任务
     * @Date: 2024-07-02
     */
    @XxlJob("syncLikedJobHandler")
    public void syncLikedJobHandler() throws Exception {
        XxlJobHelper.log("syncLikedJobHandler.start");
        try {
            subjectLikedDomainService.syncLiked();
        } catch (Exception e) {
            XxlJobHelper.log("syncLikedJobHandler.error" + e.getMessage());
        }
    }
}
