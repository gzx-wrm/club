package com.gzx.club.subject.infra.basic.service.impl;

import com.gzx.club.subject.infra.basic.entity.SubjectLiked;
import com.gzx.club.subject.infra.basic.mapper.SubjectLikedDao;
import com.gzx.club.subject.infra.basic.service.SubjectLikedService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 题目点赞表(SubjectLiked)表服务实现类
 *
 * @author makejava
 * @since 2024-07-02 13:24:48
 */
@Service("subjectLikedService")
public class SubjectLikedServiceImpl implements SubjectLikedService {
    @Resource
    private SubjectLikedDao subjectLikedDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public SubjectLiked queryById(Long id) {
        return this.subjectLikedDao.queryById(id);
    }


    /**
     * 新增数据
     *
     * @param subjectLiked 实例对象
     * @return 实例对象
     */
    @Override
    public SubjectLiked insert(SubjectLiked subjectLiked) {
        this.subjectLikedDao.insert(subjectLiked);
        return subjectLiked;
    }

    /**
     * 修改数据
     *
     * @param subjectLiked 实例对象
     * @return 实例对象
     */
    @Override
    public Integer update(SubjectLiked subjectLiked) {
        return this.subjectLikedDao.update(subjectLiked);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.subjectLikedDao.deleteById(id) > 0;
    }

    @Override
    public Integer insertBatch(LinkedList<SubjectLiked> likedLinkedList) {
        return this.subjectLikedDao.insertBatch(likedLinkedList);
    }

    @Override
    public Long countByCondition(SubjectLiked subjectLiked) {
        return this.subjectLikedDao.count(subjectLiked);
    }

    @Override
    public List<SubjectLiked> queryPageByCondition(SubjectLiked subjectLiked, Integer offset, Integer limit) {
        return this.subjectLikedDao.queryPageByCondition(subjectLiked, offset, limit);
    }

    @Override
    public Integer insertOrUpdateBatch(LinkedList<SubjectLiked> likedLinkedList) {
        return this.subjectLikedDao.insertOrUpdateBatch(likedLinkedList);
    }
}
