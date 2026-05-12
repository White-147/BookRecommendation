package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Recommend;
import com.hytc.recommendation_data.library.mapper.RecommendMapper;
import com.hytc.recommendation_data.library.model.RecommendDTO;
import com.hytc.recommendation_data.library.service.IRecommendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-22
 */
@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendMapper, Recommend> implements IRecommendService {

    @Override
    public PageUtils getRecommend(RecommendDTO queryDTO, int pageSize, int currentPage) {
        Integer pageIndex = queryDTO.getPageIndex();
        queryDTO.setPageIndex((pageIndex - 1) * queryDTO.getPageSize());

        List<Recommend> recommend = this.baseMapper.getRecommend(queryDTO);
        QueryWrapper<Recommend> wrapper = new QueryWrapper<>();
        wrapper.eq("CERT_ID",queryDTO.getCertId());
        wrapper.orderByDesc("recommend");
        Integer count = this.baseMapper.selectCount(wrapper).intValue();
        return new PageUtils(recommend, count, pageSize, currentPage);
    }
}
