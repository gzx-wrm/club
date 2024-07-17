package com.gzx.club.circle.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzx.club.circle.api.common.PageResult;
import com.gzx.club.circle.api.req.GetShareMomentReq;
import com.gzx.club.circle.api.req.RemoveShareMomentReq;
import com.gzx.club.circle.api.req.SaveMomentCircleReq;
import com.gzx.club.circle.api.vo.ShareMomentVO;
import com.gzx.club.circle.server.entity.po.ShareMoment;

/**
 * 动态信息 服务类
 */
public interface ShareMomentService extends IService<ShareMoment> {

    Boolean saveMoment(SaveMomentCircleReq req);

    PageResult<ShareMomentVO> getMoments(GetShareMomentReq req);

    Boolean removeMoment(RemoveShareMomentReq req);

    void incrReplyCount(Long id, int count);

}
