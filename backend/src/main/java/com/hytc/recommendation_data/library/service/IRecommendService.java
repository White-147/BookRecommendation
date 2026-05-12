package com.hytc.recommendation_data.library.service;

import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Recommend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.library.model.RecommendDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-22
 */
public interface IRecommendService extends IService<Recommend> {
    PageUtils getRecommend(RecommendDTO queryDTO, int pageSize, int currentPage);
}
