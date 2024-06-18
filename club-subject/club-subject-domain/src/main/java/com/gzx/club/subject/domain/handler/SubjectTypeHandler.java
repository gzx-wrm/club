package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;

public interface SubjectTypeHandler {

    SubjectTypeEnum getHandlerType();

    void add(SubjectInfoBO subjectInfoBO);

    /**
     * @Author: gzx
     * @Description: 查询
     * @Params:
     * @return:
     */

    SubjectOptionBO query(Long subjectId);
}
