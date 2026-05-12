package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.entity.extend.BookExtend;
import com.hytc.recommendation_data.library.mapper.BookMapper;
import com.hytc.recommendation_data.library.model.BookQueryDTO;
import com.hytc.recommendation_data.library.service.IBookService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-29
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

    @Override
    public PageUtils queryPage(BookQueryDTO queryDTO) {
        QueryWrapper<Book> wrapper = new QueryWrapper<Book>().like(
                StringUtils.isNotEmpty(queryDTO.getMTitle()),
                "M_TITLE", queryDTO.getMTitle());
        Page<Book> page = this.page(queryDTO.page(), wrapper);
        return new PageUtils(page);
    }

    @Override
    public Book queryBookByCallNo(String callId) {
        QueryWrapper<Book> wrapper = new QueryWrapper<Book>()
                .eq("M_CALL_NO",callId);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public void updateState(Book book) {
        QueryWrapper<Book> wrapper = new QueryWrapper<Book>()
                .eq("M_CALL_NO",book.getmCallNo());
        this.update(book,wrapper);
    }
}
