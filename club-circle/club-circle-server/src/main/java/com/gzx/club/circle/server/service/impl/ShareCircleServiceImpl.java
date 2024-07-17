package com.gzx.club.circle.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gzx.club.circle.api.enums.IsDeletedFlagEnum;
import com.gzx.club.circle.api.req.RemoveShareCircleReq;
import com.gzx.club.circle.api.req.SaveShareCircleReq;
import com.gzx.club.circle.api.req.UpdateShareCircleReq;
import com.gzx.club.circle.api.vo.ShareCircleVO;
import com.gzx.club.circle.server.dao.ShareCircleMapper;
import com.gzx.club.circle.server.entity.po.ShareCircle;
import com.gzx.club.circle.server.service.ShareCircleService;
import com.gzx.club.circle.server.util.LoginUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: club
 * @description: 圈子Service
 * @author: gzx
 * @create: 2024-07-15
 **/
@Service
public class ShareCircleServiceImpl extends ServiceImpl<ShareCircleMapper, ShareCircle> implements ShareCircleService {
    private static final Cache<Integer, List<ShareCircleVO>> CACHE = Caffeine.newBuilder().initialCapacity(1)
            .maximumSize(1).expireAfterWrite(Duration.ofSeconds(30)).build();


    @Override
    public List<ShareCircleVO> listResult() {
        // 1是CircleVO缓存的键
        List<ShareCircleVO> res = CACHE.getIfPresent(1);
        return Optional.ofNullable(res).orElseGet(() -> {
            List<ShareCircle> shareCircleList = baseMapper.selectList(Wrappers.<ShareCircle>lambdaQuery().eq(ShareCircle::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.code));
            if (CollectionUtils.isEmpty(shareCircleList)) {
                return Collections.emptyList();
            }
            List<ShareCircle> parentList = shareCircleList.stream().filter(item -> item.getParentId() == -1L).collect(Collectors.toList());
            Map<Long, List<ShareCircle>> map = shareCircleList.stream().collect(Collectors.groupingBy(ShareCircle::getParentId));
            List<ShareCircleVO> ret = parentList.stream().map(item -> {
                ShareCircleVO shareCircleVO = new ShareCircleVO();
                shareCircleVO.setCircleName(item.getCircleName());
                shareCircleVO.setIcon(item.getIcon());
                shareCircleVO.setId(item.getId());

                List<ShareCircle> children = map.get(item.getId());
                if (CollectionUtils.isEmpty(children)) {
                    shareCircleVO.setChildren(Collections.emptyList());
                    return shareCircleVO;
                }
                List<ShareCircleVO> childrenVO = children.stream().map(item1 -> {
                    ShareCircleVO shareCircleVO1 = new ShareCircleVO();
                    shareCircleVO1.setId(item1.getId());
                    shareCircleVO1.setIcon(item1.getIcon());
                    shareCircleVO1.setCircleName(item1.getCircleName());
                    shareCircleVO1.setChildren(Collections.emptyList());
                    return shareCircleVO1;
                }).collect(Collectors.toList());
                shareCircleVO.setChildren(childrenVO);

                return shareCircleVO;
            }).collect(Collectors.toList());
            CACHE.put(1, ret);
            return ret;
        });
    }

    @Override
    public Boolean saveCircle(SaveShareCircleReq req) {
        ShareCircle circle = new ShareCircle();
        circle.setCircleName(req.getCircleName());
        circle.setIcon(req.getIcon());
        circle.setParentId(req.getParentId());
        circle.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        circle.setCreatedTime(new Date());
        circle.setCreatedBy(LoginUtil.getLoginId());
        CACHE.invalidateAll();
        return save(circle);
    }

    @Override
    public Boolean updateCircle(UpdateShareCircleReq req) {
        LambdaUpdateWrapper<ShareCircle> update = Wrappers.<ShareCircle>lambdaUpdate().eq(ShareCircle::getId, req.getId())
                .eq(ShareCircle::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
                .set(Objects.nonNull(req.getParentId()), ShareCircle::getParentId, req.getParentId())
                .set(Objects.nonNull(req.getIcon()), ShareCircle::getIcon, req.getIcon())
                .set(Objects.nonNull(req.getCircleName()), ShareCircle::getCircleName, req.getCircleName())
                .set(ShareCircle::getUpdateBy, LoginUtil.getLoginId())
                .set(ShareCircle::getUpdateTime, new Date());
        boolean res = super.update(update);
        CACHE.invalidateAll();
        return res;
    }

    @Override
    public Boolean removeCircle(RemoveShareCircleReq req) {
        boolean res = super.update(Wrappers.<ShareCircle>lambdaUpdate().eq(ShareCircle::getId, req.getId())
                .eq(ShareCircle::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
                .set(ShareCircle::getIsDeleted, IsDeletedFlagEnum.DELETED.getCode()));
        super.update(Wrappers.<ShareCircle>lambdaUpdate().eq(ShareCircle::getParentId, req.getId())
                .eq(ShareCircle::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
                .set(ShareCircle::getIsDeleted, IsDeletedFlagEnum.DELETED.getCode()));
        CACHE.invalidateAll();
        return res;
    }
}
