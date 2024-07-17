package com.gzx.club.circle.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzx.club.circle.api.req.GetShareCommentReq;
import com.gzx.club.circle.api.req.RemoveShareCommentReq;
import com.gzx.club.circle.api.req.SaveShareCommentReplyReq;
import com.gzx.club.circle.api.vo.ShareCommentReplyVO;
import com.gzx.club.circle.server.entity.po.ShareCommentReply;

import java.util.List;

/**
 * 评论及回复信息 服务类
 */
public interface ShareCommentReplyService extends IService<ShareCommentReply> {

    Boolean saveComment(SaveShareCommentReplyReq req);

    Boolean removeComment(RemoveShareCommentReq req);

    List<ShareCommentReplyVO> listComment(GetShareCommentReq req);
}
