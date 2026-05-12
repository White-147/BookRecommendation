package com.hytc.recommendation_data.library.service;

import com.hytc.recommendation_data.library.entity.Collect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hytc.recommendation_data.library.model.CollectDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-09
 */
public interface ICollectService extends IService<Collect> {
    void saveCollect(String certId,String callNo);

    List<Collect> selectCollectByCertId(String certId);

    Collect queryCollect(String certId,String callNo);

    void deleteCollect(String certId, String callNo);
}
