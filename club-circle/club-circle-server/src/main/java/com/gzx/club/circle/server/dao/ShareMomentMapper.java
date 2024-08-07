package com.gzx.club.circle.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzx.club.circle.server.entity.po.ShareMoment;

/**
 * 动态信息 Mapper 接口
 */
public interface ShareMomentMapper extends BaseMapper<ShareMoment> {

    void incrReplyCount(Long id, int i);
}
