package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.convert.RadioSubjectConverter;
import com.gzx.club.subject.domain.entity.SubjectAnswerBO;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.infra.basic.entity.SubjectRadio;
import com.gzx.club.subject.infra.basic.service.SubjectRadioService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @program: club
 * @description: 单选题的处理器
 * @author: gzx
 * @create: 2024-06-17 15:41
 **/
@Component
public class RadioSubjectHandler implements SubjectTypeHandler{

    @Resource
    SubjectRadioService subjectRadioService;

    @Override
    public SubjectTypeEnum getHandlerType() {
        return SubjectTypeEnum.RADIO;
    }

    @Override
    public void add(SubjectInfoBO subjectInfoBO) {
        LinkedList<SubjectRadio> subjectRadios = new LinkedList<>();
        subjectInfoBO.getOptionList().forEach(subjectAnswerBO -> {
            SubjectRadio subjectRadio = RadioSubjectConverter.INSTANCE.convertBoToEntity(subjectAnswerBO);
            subjectRadio.setSubjectId(subjectInfoBO.getId());
            subjectRadio.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.code);
            subjectRadios.add(subjectRadio);
        });
        subjectRadioService.insertBatch(subjectRadios);
    }

    @Override
    public SubjectOptionBO query(Long subjectId) {
        SubjectRadio subjectRadio = new SubjectRadio();
        subjectRadio.setSubjectId(subjectId);
        List<SubjectRadio> result = subjectRadioService.queryByCondition(subjectRadio);
        List<SubjectAnswerBO> subjectAnswerBOList = RadioSubjectConverter.INSTANCE.convertEntitiesToBOs(result);
        SubjectOptionBO subjectOptionBO = new SubjectOptionBO();
        subjectOptionBO.setOptionList(subjectAnswerBOList);
        return subjectOptionBO;
    }
}
