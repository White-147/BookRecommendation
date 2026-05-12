package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.extend.BookExtend;
import com.hytc.recommendation_data.library.model.BookExtendDTO;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/2/13 14:11
 * @Description:
 */
public interface IBookExtendService extends IService<BookExtend> {
    PageUtils queryBook(BookExtendDTO queryDTO,int pageSize,int currentPage);

    BookExtend queryBookByCallNo(String certId,String callNo);
}
