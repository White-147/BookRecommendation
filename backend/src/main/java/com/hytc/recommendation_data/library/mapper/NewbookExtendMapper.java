package com.hytc.recommendation_data.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hytc.recommendation_data.library.entity.extend.NewbookExtend;
import com.hytc.recommendation_data.library.model.NewBookExtendDTO;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/3/3 9:05
 * @Description:
 */
public interface NewbookExtendMapper extends BaseMapper<NewbookExtend> {
    List<NewbookExtend> queryNewBook(NewBookExtendDTO queryDTO);
}
