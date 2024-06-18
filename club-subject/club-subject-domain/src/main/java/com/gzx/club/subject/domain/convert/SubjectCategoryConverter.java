package com.gzx.club.subject.domain.convert;

import com.gzx.club.subject.domain.entity.SubjectCategoryBO;
import com.gzx.club.subject.infra.basic.entity.SubjectCategory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @program: club
 * @description: 用于该层Category的转化（转化到下一层，即该层调用的下一层实体）
 * @author: gzx
 * @create: 2024-06-03 15:44
 **/
@Mapper
public interface SubjectCategoryConverter {
    SubjectCategoryConverter INSTANCE = Mappers.getMapper(SubjectCategoryConverter.class);

    SubjectCategory convertBoToCategory(SubjectCategoryBO subjectCategoryBO);

    List<SubjectCategoryBO> convertBoToCategory(List<SubjectCategory> categoryList);
}
