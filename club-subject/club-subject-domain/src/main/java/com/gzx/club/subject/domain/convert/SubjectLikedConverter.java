package com.gzx.club.subject.domain.convert;

import com.gzx.club.subject.domain.entity.SubjectLikedBO;
import com.gzx.club.subject.infra.basic.entity.SubjectLiked;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 题目点赞表 bo转换器
 *
 * @author gzx
 * @since 2024-01-07 23:08:45
 */
@Mapper
public interface SubjectLikedConverter {

    SubjectLikedConverter INSTANCE = Mappers.getMapper(SubjectLikedConverter.class);

    SubjectLiked convertBOToEntity(SubjectLikedBO subjectLikedBO);

    SubjectLikedBO convertEntityToBO(SubjectLiked subjectLiked);

    List<SubjectLikedBO> convertEntityToBO(List<SubjectLiked> subjectLiked);

}
