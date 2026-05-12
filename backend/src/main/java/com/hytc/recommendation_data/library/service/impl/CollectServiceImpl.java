package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hytc.recommendation_data.library.entity.Collect;
import com.hytc.recommendation_data.library.mapper.CollectMapper;
import com.hytc.recommendation_data.library.model.CollectDTO;
import com.hytc.recommendation_data.library.service.ICollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-09
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements ICollectService {

    @Autowired
    private CollectMapper collectMapper;


    @Override
    public void saveCollect(String certId, String callNo) {
        Collect collect = new Collect();
        collect.setCertId(certId);
        collect.setCallNo(callNo);
        this.baseMapper.insert(collect);
    }

    public List<Collect> selectCollectByCertId(String certId){
        return collectMapper.queryCollectByCertId(certId);
    }

    @Override
    public Collect queryCollect(String certId, String callNo) {
        return collectMapper.queryCollect(certId,callNo);
    }

    @Override
    public void deleteCollect(String certId, String callNo){
        QueryWrapper<Collect> wrapper = new QueryWrapper<>();
        wrapper.eq("CERT_ID",certId);
        wrapper.eq("CALL_NO",callNo);
        this.baseMapper.delete(wrapper);
    }
}
