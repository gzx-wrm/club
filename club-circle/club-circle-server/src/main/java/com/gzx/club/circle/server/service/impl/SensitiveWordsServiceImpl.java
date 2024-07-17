package com.gzx.club.circle.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzx.club.circle.server.dao.SensitiveWordsMapper;
import com.gzx.club.circle.server.entity.po.SensitiveWords;
import com.gzx.club.circle.server.service.SensitiveWordsService;
import org.springframework.stereotype.Service;

/**
 * 敏感词表 服务实现类
 */
@Service
public class SensitiveWordsServiceImpl extends ServiceImpl<SensitiveWordsMapper, SensitiveWords> implements SensitiveWordsService {

}
