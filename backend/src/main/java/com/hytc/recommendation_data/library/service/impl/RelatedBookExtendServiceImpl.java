package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.library.entity.extend.RelatedBookExtend;
import com.hytc.recommendation_data.library.mapper.RelatedBookExtendMapper;
import com.hytc.recommendation_data.library.service.IRelatedBookExtendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/3/4 10:15
 * @Description:
 */
@Service
public class RelatedBookExtendServiceImpl
        extends ServiceImpl<RelatedBookExtendMapper, RelatedBookExtend>
        implements IRelatedBookExtendService {
    @Override
    public List<RelatedBookExtend> queryRelatedBook(String certId, String callNo) {
        return this.baseMapper.queryRelatedBook(certId, callNo);
    }
}