package com.gzx.club.subject.infra.basic.service;

import com.gzx.club.subject.infra.basic.entity.EsSubjectInfo;

import java.util.List;

/**
 * @program: club
 * @description: ES题目类服务接口
 * @author: gzx
 * @create: 2024-06-30
 **/
public interface SubjectEsService {

    boolean insert(EsSubjectInfo esSubjectInfo);

    List<EsSubjectInfo> querySubjectList(EsSubjectInfo esSubjectInfo, Integer from, Integer size);

}
