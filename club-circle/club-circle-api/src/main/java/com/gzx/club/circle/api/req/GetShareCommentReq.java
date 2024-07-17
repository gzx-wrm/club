package com.gzx.club.circle.api.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 鸡圈内容信息
 */
@Getter
@Setter
public class GetShareCommentReq implements Serializable {

    /**
     * 圈子id
     */
    private Long id;

}
