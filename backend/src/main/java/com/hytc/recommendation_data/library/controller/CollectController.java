package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.library.entity.Collect;
import com.hytc.recommendation_data.library.service.ICollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-09
 */
@Api(tags = "用户收藏", value = "Collect")
@RestController
@RequestMapping("/library/collect")
@Log4j
public class CollectController {
    @Autowired
    private ICollectService collectService;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @ApiOperation(value = "新增收藏信息", notes = "新增收藏")
    @PostMapping("/saveCollect")
    public String saveCollect(@RequestParam("certId") String certId, @RequestParam("callNo") String callNo) {
        collectService.saveCollect(certId, callNo);
        String str = certId + "," + callNo + "," + "addCollect";
        kafkaTemplate.send("userLog", str);
        return "success";
    }

    @ApiOperation(value = "查询用户收藏信息", notes = "用户收藏信息")
    @GetMapping("/queryByCertId")
    public List<Collect> queryByCertId(@RequestParam("certId") String certId) {
        return collectService.selectCollectByCertId(certId);
    }

    @ApiOperation(value = "查询收藏信息", notes = "收藏信息")
    @GetMapping("/queryCollect")
    public Collect queryCollect(@RequestParam("certId") String certId, @RequestParam("callNo") String callNo) {
        return collectService.queryCollect(certId, callNo);
    }

    @ApiOperation(value = "删除收藏信息", notes = "删除信息")
    @PostMapping("/deleteCollect")
    public String deleteCollect(@RequestParam("certId") String certId, @RequestParam("callNo") String callNo) {
        collectService.deleteCollect(certId, callNo);
        return "success";
    }

}
