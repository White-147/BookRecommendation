package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.extend.NewbookExtend;
import com.hytc.recommendation_data.library.model.NewBookExtendDTO;

/**
 * @Author: White Jiang
 * @Date: 2023/3/3 9:12
 * @Description:
 */
public interface INewbookExtendService extends IService<NewbookExtend> {
    PageUtils queryNewBook(NewBookExtendDTO queryDTO, int pageSize, int currentPage);
}
