package com.hytc.recommendation_data.library.mapper;

import com.hytc.recommendation_data.library.entity.Recommend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hytc.recommendation_data.library.model.RecommendDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-22
 */
public interface RecommendMapper extends BaseMapper<Recommend> {
    List<Recommend> getRecommend(RecommendDTO queryDTO);
}
