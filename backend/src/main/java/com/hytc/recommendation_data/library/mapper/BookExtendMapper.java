package com.hytc.recommendation_data.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hytc.recommendation_data.library.entity.extend.BookExtend;
import com.hytc.recommendation_data.library.model.BookExtendDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/2/13 14:10
 * @Description:
 */
public interface BookExtendMapper extends BaseMapper<BookExtend> {
    List<BookExtend> queryBook(BookExtendDTO queryDTO);

    BookExtend queryBookByCallNo(@Param("certId") String certId, @Param("callNo") String callNo);
}
