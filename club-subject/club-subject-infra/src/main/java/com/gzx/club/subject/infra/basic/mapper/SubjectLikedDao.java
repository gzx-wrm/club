package com.gzx.club.subject.infra.basic.mapper;

import com.gzx.club.subject.infra.basic.entity.SubjectLiked;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目点赞表(SubjectLiked)表数据库访问层
 *
 * @author makejava
 * @since 2024-06-03 15:07:19
 */
public interface SubjectLikedDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SubjectLiked queryById(Long id);


    /**
     * 统计总行数
     *
     * @param subjectLiked 查询条件
     * @return 总行数
     */
    long count(SubjectLiked subjectLiked);

    /**
     * 新增数据
     *
     * @param subjectLiked 实例对象
     * @return 影响行数
     */
    int insert(SubjectLiked subjectLiked);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SubjectLiked> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SubjectLiked> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SubjectLiked> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<SubjectLiked> entities);

    /**
     * 修改数据
     *
     * @param subjectLiked 实例对象
     * @return 影响行数
     */
    int update(SubjectLiked subjectLiked);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

}

