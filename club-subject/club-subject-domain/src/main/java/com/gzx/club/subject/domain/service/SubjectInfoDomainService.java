package com.gzx.club.subject.domain.service;

import com.gzx.club.subject.common.entity.PageResult;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.infra.basic.entity.EsSubjectInfo;
import com.gzx.club.subject.infra.basic.entity.SubjectInfo;

import java.util.List;

public interface SubjectInfoDomainService {

    void add(SubjectInfoBO subjectInfoBO);

    PageResult<SubjectInfoBO> getSubjectPage(SubjectInfoBO subjectInfoBO);

    SubjectInfoBO querySubjectInfo(SubjectInfoBO subjectInfoBO);

    /**
     * 全文检索
     */
    PageResult<EsSubjectInfo> getSubjectPageBySearch(SubjectInfoBO subjectInfoBO);

    List<SubjectInfoBO> getContributeList();
}
