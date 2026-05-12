package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Lend;
import com.hytc.recommendation_data.library.model.LendQueryDTO;
import com.hytc.recommendation_data.library.service.ILendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
 * @since 2023-01-29
 */
@Api(tags = "借阅信息", value = "Lend")
@RestController
@RequestMapping("/library/lend")
@Log4j
public class LendController {
    @Autowired
    private ILendService lendService;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @ApiOperation(value = "查询分页借阅", notes = "借阅信息")
    @GetMapping("/list")
    public PageUtils list(@ApiParam("查询的条件") LendQueryDTO queryDTO) {
        return lendService.queryPage(queryDTO);
    }

    @ApiOperation(value = "查询分页借阅", notes = "借阅信息")
    @GetMapping("/queryLends")
    public List<Lend> queryLends(@RequestParam("certId") String certId) {
        return lendService.queryLends(certId);
    }

    @ApiOperation(value = "添加借阅信息", notes = "添加记录")
    @PostMapping("/save")
    public String save(@RequestParam("callNo") String callNo, @RequestParam("account") String account) {
        lendService.saveLend(callNo, account);
        Lend lend = lendService.queryLend(callNo, account);
        String str = lend.getCertId() + "," + callNo + "," + "addLend";
        kafkaTemplate.send("userLog", str);
        return "success";
    }

    @ApiOperation(value = "删除借阅信息", notes = "删除记录")
    @PostMapping("/delete")
    public String delete(@RequestParam("certId") String certId, @RequestParam("callNo") String callNo) {
        lendService.deleteLend(certId, callNo);
        return "success";
    }
}