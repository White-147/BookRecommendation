package com.hytc.recommendation_data.library.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.library.entity.RelatedBook;
import com.hytc.recommendation_data.library.mapper.RelatedBookMapper;
import com.hytc.recommendation_data.library.service.IRelatedBookService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-03-01
 */
@Service
public class RelatedBookServiceImpl extends ServiceImpl<RelatedBookMapper, RelatedBook> implements IRelatedBookService {

}
