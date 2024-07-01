package com.gzx.club.subject.application.convert;

import com.gzx.club.subject.application.dto.SubjectInfoDTO;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubjectInfoDTOConverter {

    SubjectInfoDTOConverter INSTANCE = Mappers.getMapper(SubjectInfoDTOConverter.class);

    SubjectInfoBO convertDTOToBO(SubjectInfoDTO subjectInfoDTO);

    List<SubjectInfoBO> convertDTOToBO(List<SubjectInfoDTO> subjectInfoDTO);

    SubjectInfoDTO convertBOToDTO(SubjectInfoBO subjectInfoBO);

    List<SubjectInfoDTO> convertBOToDTO(List<SubjectInfoBO> subjectInfoBO);
}
