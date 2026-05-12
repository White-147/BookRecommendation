package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.library.entity.extend.RelatedBookExtend;

import java.util.List;

/**
 * @Author: White Jiang
 * @Date: 2023/3/4 10:15
 * @Description:
 */
public interface IRelatedBookExtendService extends IService<RelatedBookExtend> {
    List<RelatedBookExtend> queryRelatedBook(String certId, String callNo);
}
