package com.gzx.club.subject.domain.convert;

import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.infra.basic.entity.SubjectBrief;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-18T22:08:13+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
public class BriefSubjectConverterImpl implements BriefSubjectConverter {

    @Override
    public SubjectBrief convertBoToEntity(SubjectInfoBO subjectInfoBO) {
        if ( subjectInfoBO == null ) {
            return null;
        }

        SubjectBrief subjectBrief = new SubjectBrief();

        subjectBrief.setId( subjectInfoBO.getId() );
        subjectBrief.setSubjectAnswer( subjectInfoBO.getSubjectAnswer() );

        return subjectBrief;
    }
}
