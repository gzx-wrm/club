package com.gzx.club.subject.domain.convert;


import com.gzx.club.subject.domain.entity.SubjectAnswerBO;
import com.gzx.club.subject.infra.basic.entity.SubjectMultiple;
import com.gzx.club.subject.infra.basic.entity.SubjectRadio;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MultipleSubjectConverter {

    MultipleSubjectConverter INSTANCE = Mappers.getMapper(MultipleSubjectConverter.class);

    SubjectMultiple convertBoToEntity(SubjectAnswerBO subjectAnswerBO);

    List<SubjectAnswerBO> convertEntitiesToBOs(List<SubjectMultiple> subjectMultipleList);

}
