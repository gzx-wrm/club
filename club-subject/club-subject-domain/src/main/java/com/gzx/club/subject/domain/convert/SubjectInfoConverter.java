package com.gzx.club.subject.domain.convert;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.infra.basic.entity.SubjectInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubjectInfoConverter {

    SubjectInfoConverter INSTANCE = Mappers.getMapper(SubjectInfoConverter.class);

    SubjectInfo convertBoToInfo(SubjectInfoBO subjectInfoBO);

    List<SubjectInfoBO> convertInfosToBos(List<SubjectInfo> subjectInfos);

    SubjectInfoBO convertOptionAndInfoToBo(SubjectOptionBO subjectOptionBO, SubjectInfo subjectInfo);
}
