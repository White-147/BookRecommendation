package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.library.entity.Reader;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-26
 */
public interface IReaderService extends IService<Reader> {
    boolean checkCertId(String certId);
}
