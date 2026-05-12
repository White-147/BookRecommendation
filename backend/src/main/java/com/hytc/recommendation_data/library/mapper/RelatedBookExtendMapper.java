package com.hytc.recommendation_data.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hytc.recommendation_data.library.entity.extend.RelatedBookExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/3/1 11:02
 * @Description:
 */
public interface RelatedBookExtendMapper extends BaseMapper<RelatedBookExtend> {
    List<RelatedBookExtend> queryRelatedBook(@Param("certId") String certId,
                                             @Param("callNo") String callNo);
}
