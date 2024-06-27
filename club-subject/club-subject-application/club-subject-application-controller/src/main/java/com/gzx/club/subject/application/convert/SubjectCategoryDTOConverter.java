package com.gzx.club.subject.application.convert;

import com.gzx.club.subject.application.dto.SubjectCategoryDTO;
import com.gzx.club.subject.domain.entity.SubjectCategoryBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    /**
     * @Author: gzx
     *
     * @Description: 使用Mapping注解，可以将不同名称的属性进行映射，如果类型也不同的话会尝试调用简单映射方法或者该类的其他方法进行映射
     *               如果还是不能满足要求，可以自定义转换方法qualifiedByName、@Named
     * @Date: 2024-06-26
     */
    @Mapping(source = "labelBOList", target = "labelDTOList")
    SubjectCategoryDTO convertBoToCategory(SubjectCategoryBO subjectCategoryBO);

    @Mapping(source = "labelBOList", target = "labelDTOList")
    List<SubjectCategoryDTO> convertBoToCategoryDTOList(List<SubjectCategoryBO> subjectCategoryBOs);

    @Mapping(source = "labelDTOList", target = "labelBOList")
    SubjectCategoryBO convertDtoToCategoryBO(SubjectCategoryDTO subjectCategoryDTO);
}
