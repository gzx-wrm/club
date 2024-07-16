package com.gzx.club.subject.infra.basic.service;

import com.gzx.club.subject.infra.basic.entity.SubjectInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目信息表(SubjectInfo)表服务接口
 *
 * @author makejava
 * @since 2024-06-03 15:07:19
 */
public interface SubjectInfoService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SubjectInfo queryById(Long id);

    /**
     * @Author: gzx
     * @Description: 条件查询记录数
     * @Params:
     * @return:
     */
    long countByCondition(SubjectInfo subjectInfo,
                         Long categoryId,
                         Long labelId);

    /**
     * @Author: gzx
     * @Description: 条件+分页查询
     * @Params:
     * @return:
     */

    List<SubjectInfo> queryPage(SubjectInfo subjectInfo, Long categoryId, Long labelId, Integer offset, Integer limit);

    /**
     * 新增数据
     *
     * @param subjectInfo 实例对象
     * @return 实例对象
     */
    SubjectInfo insert(SubjectInfo subjectInfo);

    /**
     * 修改数据
     *
     * @param subjectInfo 实例对象
     * @return 实例对象
     */
    SubjectInfo update(SubjectInfo subjectInfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    Long querySubjectIdCursor(Long subjectId, Long categoryId, Long labelId, Integer flag);
}
