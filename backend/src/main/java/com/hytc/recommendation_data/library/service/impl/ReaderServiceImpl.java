package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.library.entity.Reader;
import com.hytc.recommendation_data.library.mapper.ReaderMapper;
import com.hytc.recommendation_data.library.service.IReaderService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-26
 */
@Service
public class ReaderServiceImpl extends ServiceImpl<ReaderMapper, Reader> implements IReaderService {

    @Override
    public boolean checkCertId(String certId) {
        return this.getById(certId) != null;
    }
}
