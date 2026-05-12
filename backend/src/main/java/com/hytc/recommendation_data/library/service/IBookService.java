package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.model.BookQueryDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-29
 */
public interface IBookService extends IService<Book> {
    PageUtils queryPage(BookQueryDTO queryDTO);

    Book queryBookByCallNo(String callId);

    void updateState(Book book);
}
