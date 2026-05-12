package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.extend.BookExtend;
import com.hytc.recommendation_data.library.mapper.BookExtendMapper;
import com.hytc.recommendation_data.library.model.BookExtendDTO;
import com.hytc.recommendation_data.library.service.IBookExtendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/2/13 14:12
 * @Description:
 */

@Service
public class BookExtendServiceImpl
        extends ServiceImpl<BookExtendMapper, BookExtend>
        implements IBookExtendService {
    @Override
    public PageUtils queryBook(BookExtendDTO queryDTO, int pageSize, int currentPage) {
        Integer pageIndex = queryDTO.getPageIndex();
        queryDTO.setPageIndex((pageIndex - 1) * queryDTO.getPageSize());
        List<BookExtend> bookExtends = this.baseMapper.queryBook(queryDTO);
        QueryWrapper<BookExtend> wrapper = new QueryWrapper<>();
        int count = this.baseMapper.selectCount(wrapper).intValue();
        return new PageUtils(bookExtends, count, pageSize, currentPage);
    }

    @Override
    public BookExtend queryBookByCallNo(String certId, String callNo) {
        return this.baseMapper.queryBookByCallNo(certId, callNo);
    }
}
