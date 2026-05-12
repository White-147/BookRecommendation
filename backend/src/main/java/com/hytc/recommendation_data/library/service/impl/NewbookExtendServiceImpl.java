package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Newbook;
import com.hytc.recommendation_data.library.entity.extend.NewbookExtend;
import com.hytc.recommendation_data.library.mapper.NewbookExtendMapper;
import com.hytc.recommendation_data.library.model.NewBookExtendDTO;
import com.hytc.recommendation_data.library.service.INewbookExtendService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/3/3 9:13
 * @Description:
 */
@Service
public class NewbookExtendServiceImpl
        extends ServiceImpl<NewbookExtendMapper, NewbookExtend>
        implements INewbookExtendService {

    @Override
    public PageUtils queryNewBook(NewBookExtendDTO queryDTO, int pageSize, int currentPage) {
        Integer pageIndex = queryDTO.getPageIndex();
        queryDTO.setPageIndex((pageIndex - 1) * queryDTO.getPageSize());
        List<NewbookExtend> newBooks = this.baseMapper.queryNewBook(queryDTO);
        QueryWrapper<NewbookExtend> wrapper = new QueryWrapper<>();
        Integer count = this.baseMapper.selectCount(wrapper).intValue();
        return new PageUtils(newBooks, count, pageSize, currentPage);
    }
}
