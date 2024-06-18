package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.IsDeletedFlagEnum;
import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.convert.JudgeSubjectConverter;
import com.gzx.club.subject.domain.entity.SubjectAnswerBO;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import com.gzx.club.subject.domain.entity.SubjectOptionBO;
import com.gzx.club.subject.infra.basic.entity.SubjectJudge;
import com.gzx.club.subject.infra.basic.service.SubjectJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 判断题目的策略类
 */
@Component
@Slf4j
public class JudgeTypeHandler implements SubjectTypeHandler{

    @Resource
    SubjectJudgeService subjectJudgeService;
    
    @Override
    public SubjectTypeEnum getHandlerType() {
        return SubjectTypeEnum.JUDGE;
    }

    @Override
    public void add(SubjectInfoBO subjectInfoBO) {
        try {
            //判断题目的插入
            SubjectJudge subjectJudge = new SubjectJudge();
            SubjectAnswerBO subjectAnswerBO = subjectInfoBO.getOptionList().get(0);
            subjectJudge.setSubjectId(subjectInfoBO.getId());
            subjectJudge.setIsCorrect(subjectAnswerBO.getIsCorrect());
            subjectJudge.setIsDeleted(IsDeletedFlagEnum.UN_DELETED.getCode());
            subjectJudgeService.insert(subjectJudge);
        } catch (Exception exception) {
            log.error("插入判断题出错, {}", exception.getMessage());
            throw exception;
        }
    }

    @Override
    public SubjectOptionBO query(Long subjectId) {
        SubjectJudge subjectJudge = new SubjectJudge();
        subjectJudge.setSubjectId(subjectId);
        List<SubjectJudge> result = subjectJudgeService.queryByCondition(subjectJudge);
        List<SubjectAnswerBO> subjectAnswerBOList = JudgeSubjectConverter.INSTANCE.convertEntityToBoList(result);
        SubjectOptionBO subjectOptionBO = new SubjectOptionBO();
        subjectOptionBO.setOptionList(subjectAnswerBOList);
        return subjectOptionBO;
    }
}
