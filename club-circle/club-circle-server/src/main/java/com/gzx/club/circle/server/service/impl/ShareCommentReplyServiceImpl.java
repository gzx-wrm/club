package com.gzx.club.circle.server.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzx.club.circle.api.enums.IsDeletedFlagEnum;
import com.gzx.club.circle.api.req.GetShareCommentReq;
import com.gzx.club.circle.api.req.RemoveShareCommentReq;
import com.gzx.club.circle.api.req.SaveShareCommentReplyReq;
import com.gzx.club.circle.api.vo.ShareCommentReplyVO;
import com.gzx.club.circle.server.dao.ShareCommentReplyMapper;
import com.gzx.club.circle.server.dao.ShareMomentMapper;
import com.gzx.club.circle.server.entity.dto.UserInfo;
import com.gzx.club.circle.server.entity.po.ShareCommentReply;
import com.gzx.club.circle.server.entity.po.ShareMoment;
import com.gzx.club.circle.server.rpc.UserRpc;
import com.gzx.club.circle.server.service.ShareCommentReplyService;
import com.gzx.club.circle.server.util.LoginUtil;
import com.gzx.club.circle.server.util.TreeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论及回复信息 服务实现类
 */
@Service
public class ShareCommentReplyServiceImpl extends ServiceImpl<ShareCommentReplyMapper, ShareCommentReply> implements ShareCommentReplyService {

    @Resource
    private ShareMomentMapper shareMomentMapper;

    @Resource
    private UserRpc userRpc;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveComment(SaveShareCommentReplyReq req) {
        ShareMoment moment = shareMomentMapper.selectById(req.getMomentId());
        ShareCommentReply comment = new ShareCommentReply();
        comment.setMomentId(req.getMomentId());
        comment.setReplyType(req.getReplyType());
        String loginId = LoginUtil.getLoginId();
        // 1评论 2回复
        if (req.getReplyType() == 1) {
            comment.setParentId(-1L);
            comment.setToId(req.getTargetId());
            comment.setToUser(loginId);
            comment.setToUserAuthor(Objects.nonNull(moment.getCreatedBy()) && loginId.equals(moment.getCreatedBy()) ? 1 : 0);
        } else {
            comment.setParentId(req.getTargetId());
            comment.setReplyId(req.getTargetId());
            comment.setReplyUser(loginId);
            comment.setReplayAuthor(Objects.nonNull(moment.getCreatedBy()) && loginId.equals(moment.getCreatedBy()) ? 1 : 0);
        }
        comment.setContent(req.getContent());
        if (!CollectionUtils.isEmpty(req.getPicUrlList())) {
            comment.setPicUrls(JSON.toJSONString(req.getPicUrlList()));
        }
        comment.setCreatedBy(LoginUtil.getLoginId());
        comment.setCreatedTime(new Date());
        comment.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());

        shareMomentMapper.incrReplyCount(moment.getId(), 1);
        return super.save(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeComment(RemoveShareCommentReq req) {
        ShareCommentReply comment = getById(req.getId());
        LambdaQueryWrapper<ShareCommentReply> query = Wrappers.<ShareCommentReply>lambdaQuery()
                .eq(ShareCommentReply::getMomentId, comment.getMomentId())
                .eq(ShareCommentReply::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
                .select(ShareCommentReply::getId,
                        ShareCommentReply::getMomentId,
                        ShareCommentReply::getReplyType,
                        ShareCommentReply::getContent,
                        ShareCommentReply::getPicUrls,
                        ShareCommentReply::getCreatedBy,
                        ShareCommentReply::getToUser,
                        ShareCommentReply::getParentId);
        List<ShareCommentReply> list = list(query);
        List<ShareCommentReply> replyList = new ArrayList<>();
        List<ShareCommentReply> tree = TreeUtils.buildTree(list);
        for (ShareCommentReply reply : tree) {
            TreeUtils.findAll(replyList, reply, req.getId());
        }
        // 关联子级对象及 moment 的回复数量
        Set<Long> ids = replyList.stream().map(ShareCommentReply::getId).collect(Collectors.toSet());
        LambdaUpdateWrapper<ShareCommentReply> update = Wrappers.<ShareCommentReply>lambdaUpdate()
                .eq(ShareCommentReply::getMomentId, comment.getMomentId())
                .in(ShareCommentReply::getId, ids);
        ShareCommentReply updateEntity = new ShareCommentReply();
        updateEntity.setIsDeleted(IsDeletedFlagEnum.DELETED.getCode());
        int count = getBaseMapper().update(updateEntity, update);
        shareMomentMapper.incrReplyCount(comment.getMomentId(), -count);
        return true;
    }

    @Override
    public List<ShareCommentReplyVO> listComment(GetShareCommentReq req) {
        LambdaQueryWrapper<ShareCommentReply> query = Wrappers.<ShareCommentReply>lambdaQuery()
                .eq(ShareCommentReply::getMomentId, req.getId())
                .eq(ShareCommentReply::getIsDeleted, IsDeletedFlagEnum.UN_DELETED.getCode())
                .select(ShareCommentReply::getId,
                        ShareCommentReply::getMomentId,
                        ShareCommentReply::getReplyType,
                        ShareCommentReply::getContent,
                        ShareCommentReply::getPicUrls,
                        ShareCommentReply::getCreatedBy,
                        ShareCommentReply::getToUser,
                        ShareCommentReply::getCreatedTime,
                        ShareCommentReply::getParentId);
        List<ShareCommentReply> list = list(query);
        List<String> usernameList = list.stream().map(ShareCommentReply::getCreatedBy).collect(Collectors.toList());
        Map<String, UserInfo> userInfoMap = userRpc.batchGetUserInfo(usernameList);
        UserInfo defaultUser = new UserInfo();

        List<ShareCommentReplyVO> voList = list.stream().map(item -> {
            ShareCommentReplyVO vo = new ShareCommentReplyVO();
            vo.setId(item.getId());
            vo.setMomentId(item.getMomentId());
            vo.setReplyType(item.getReplyType());
            vo.setContent(item.getContent());
            vo.setAvatar(userInfoMap.getOrDefault(item.getCreatedBy(), defaultUser).getAvatar());
            vo.setCreatedTime(item.getCreatedTime().getTime());
            vo.setUserName(userInfoMap.getOrDefault(item.getCreatedBy(), defaultUser).getUserName());
            if (Objects.nonNull(item.getPicUrls())) {
                vo.setPicUrlList(JSONArray.parseArray(item.getPicUrls(), String.class));
            }
            if (item.getReplyType() == 2) {
                vo.setFromId(item.getCreatedBy());
                vo.setToId(item.getToUser());
            }
            vo.setParentId(item.getParentId());
            return vo;
        }).collect(Collectors.toList());
        return TreeUtils.buildTree(voList);
    }

}
