package com.hytc.recommendation_data.library.mapper;

import com.hytc.recommendation_data.library.entity.Collect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-09
 */

public interface CollectMapper extends BaseMapper<Collect> {
    List<Collect> queryCollectByCertId(@Param("certId")String certId);

    Collect queryCollect(@Param("certId")String certId,@Param("callNo")String callNo);
}
