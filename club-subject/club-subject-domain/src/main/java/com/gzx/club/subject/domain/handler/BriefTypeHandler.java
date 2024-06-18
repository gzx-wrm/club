package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.convert.BriefSubjectConverter;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.infra.basic.entity.SubjectBrief;
import com.gzx.club.subject.infra.basic.service.SubjectBriefService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 简答题目的策略类
 */
@Component
public class BriefTypeHandler implements SubjectTypeHandler{

    @Resource
    SubjectBriefService subjectBriefService;
    
    @Override
    public SubjectTypeEnum getHandlerType() {
        return SubjectTypeEnum.BRIEF;
    }

    @Override
    public void add(SubjectInfoBO subjectInfoBO) {
        SubjectBrief subjectBrief = BriefSubjectConverter.INSTANCE.convertBoToEntity(subjectInfoBO);
        subjectBrief.setSubjectId(subjectInfoBO.getId());
        subjectBrief.setSubjectAnswer(subjectInfoBO.getSubjectAnswer());
        subjectBrief.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
        subjectBriefService.insert(subjectBrief);
    }

    @Override
    public SubjectOptionBO query(Long subjectId) {
        SubjectBrief subjectBrief = new SubjectBrief();
        subjectBrief.setSubjectId(subjectId);
        SubjectBrief result = subjectBriefService.queryByCondition(subjectBrief).get(0);
        SubjectOptionBO subjectOptionBO = new SubjectOptionBO();
        subjectOptionBO.setSubjectAnswer(result.getSubjectAnswer());
        return subjectOptionBO;
    }
}
