package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.library.entity.Newbook;
import com.hytc.recommendation_data.library.mapper.NewbookMapper;
import com.hytc.recommendation_data.library.service.INewbookService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-03-02
 */
@Service
public class NewbookServiceImpl extends ServiceImpl<NewbookMapper, Newbook> implements INewbookService {
}
