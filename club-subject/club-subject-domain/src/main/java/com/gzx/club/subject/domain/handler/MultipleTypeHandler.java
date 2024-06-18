package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.convert.MultipleSubjectConverter;
import com.gzx.club.subject.domain.convert.RadioSubjectConverter;
import com.gzx.club.subject.domain.entity.SubjectAnswerBO;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.infra.basic.entity.SubjectMultiple;
import com.gzx.club.subject.infra.basic.entity.SubjectRadio;
import com.gzx.club.subject.infra.basic.service.SubjectMultipleService;
import com.gzx.club.subject.infra.basic.service.SubjectRadioService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 多选题目的策略类
 */
@Component
public class MultipleTypeHandler implements SubjectTypeHandler{

    @Resource
    SubjectMultipleService subjectMultipleService;

    @Override
    public SubjectTypeEnum getHandlerType() {
        return SubjectTypeEnum.MULTIPLE;
    }

    @Override
    public void add(SubjectInfoBO subjectInfoBO) {
        LinkedList<SubjectMultiple> subjectMultiples = new LinkedList<>();
        subjectInfoBO.getOptionList().forEach(subjectAnswerBO -> {
            SubjectMultiple subjectMultiple = MultipleSubjectConverter.INSTANCE.convertBoToEntity(subjectAnswerBO);
            subjectMultiple.setSubjectId(subjectInfoBO.getId());
            subjectMultiple.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
            subjectMultiples.add(subjectMultiple);
        });
        subjectMultipleService.insertBatch(subjectMultiples);
    }

    @Override
    public SubjectOptionBO query(Long subjectId) {
        SubjectMultiple subjectMultiple = new SubjectMultiple();
        subjectMultiple.setSubjectId(subjectId);
        List<SubjectMultiple> result = subjectMultipleService.queryByCondition(subjectMultiple);
        List<SubjectAnswerBO> subjectAnswerBOList = MultipleSubjectConverter.INSTANCE.convertEntitiesToBOs(result);
        SubjectOptionBO subjectOptionBO = new SubjectOptionBO();
        subjectOptionBO.setOptionList(subjectAnswerBOList);
        return subjectOptionBO;
    }
}
