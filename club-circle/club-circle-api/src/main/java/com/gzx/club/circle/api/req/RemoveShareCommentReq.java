package com.gzx.club.circle.api.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 鸡圈内容信息
 */
@Getter
@Setter
public class RemoveShareCommentReq implements Serializable {

    private Long id;

    /**
     * 回复类型 1评论 2回复
     */
    private Integer replyType;

}
