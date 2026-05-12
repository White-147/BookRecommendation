package com.hytc.recommendation_data.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Lend;
import com.hytc.recommendation_data.library.model.LendQueryDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-29
 */
public interface ILendService extends IService<Lend> {
    PageUtils queryPage(LendQueryDTO queryDTO);

    List<Lend> queryLends(String certId);

    void saveLend(String callNo, String account);

    Lend queryLend(String callNo,String account);

    void deleteLend(String certId,String callNo);
}
