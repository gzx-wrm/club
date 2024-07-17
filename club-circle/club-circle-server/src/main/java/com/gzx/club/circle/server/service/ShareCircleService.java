package com.gzx.club.circle.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzx.club.circle.api.req.RemoveShareCircleReq;
import com.gzx.club.circle.api.req.SaveShareCircleReq;
import com.gzx.club.circle.api.req.UpdateShareCircleReq;
import com.gzx.club.circle.api.vo.ShareCircleVO;
import com.gzx.club.circle.server.entity.po.ShareCircle;

import java.util.List;

/**
 * @program: club
 * @description: 圈子Service
 * @author: gzx
 * @create: 2024-07-15
 **/
public interface ShareCircleService extends IService<ShareCircle> {

    List<ShareCircleVO> listResult();

    Boolean saveCircle(SaveShareCircleReq req);

    Boolean updateCircle(UpdateShareCircleReq req);

    Boolean removeCircle(RemoveShareCircleReq req);
}
