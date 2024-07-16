package com.gzx.club.practice.server.service;

import com.gzx.club.practice.api.vo.SpecialPracticeVO;

import java.util.List;

/**
 * @program: club
 * @description: 习题册服务
 * @author: gzx
 * @create: 2024-07-07
 **/
public interface PracticeSetService {

    /**
     * 获取专项练习内容
     */
    List<SpecialPracticeVO> getSpecialPracticeContent();
}
