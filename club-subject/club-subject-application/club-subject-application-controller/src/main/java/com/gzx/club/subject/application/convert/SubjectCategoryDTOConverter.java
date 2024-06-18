package com.gzx.club.subject.application.convert;

import com.gzx.club.subject.application.dto.SubjectCategoryDTO;
import com.gzx.club.subject.domain.entity.SubjectCategoryBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @program: club
 * @description:
 * @author: gzx
 * @create: 2024-06-03 16:24
 **/
@Mapper
public interface SubjectCategoryDTOConverter {

    SubjectCategoryDTOConverter INSTANCE = Mappers.getMapper(SubjectCategoryDTOConverter.class);

    SubjectCategoryDTO convertBoToCategory(SubjectCategoryBO subjectCategoryBO);

    List<SubjectCategoryDTO> convertBoToCategoryDTOList(List<SubjectCategoryBO> subjectCategoryDTO);

    SubjectCategoryBO convertDtoToCategoryBO(SubjectCategoryDTO subjectCategoryDTO);
}
