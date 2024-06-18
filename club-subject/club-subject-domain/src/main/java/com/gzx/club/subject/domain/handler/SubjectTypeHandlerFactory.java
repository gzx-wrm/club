package com.gzx.club.subject.domain.handler;

import com.gzx.club.subject.common.enums.SubjectTypeEnum;
import com.gzx.club.subject.domain.entity.SubjectInfoBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: club
 * @description: 题目工厂
 * @author: gzx
 * @create: 2024-06-17 15:59
 **/
@Component
public class SubjectTypeHandlerFactory implements InitializingBean {

    @Resource
    List<SubjectTypeHandler> subjectTypeHandlerList;

    Map<SubjectTypeEnum, SubjectTypeHandler> map;

    @Override
    public void afterPropertiesSet() throws Exception {
        map = new HashMap<>();
        subjectTypeHandlerList.forEach(subjectTypeHandler -> map.put(subjectTypeHandler.getHandlerType(), subjectTypeHandler));
    }

    public SubjectTypeHandler getHandler(int subjectType) {
        return map.get(SubjectTypeEnum.getByCode(subjectType));
    }

}
