package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.library.service.IReaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-26
 */
@Api(tags = "读者信息", value = "Reader")
@RestController
@RequestMapping("/library/reader")
public class ReaderController {

    @Autowired
    private IReaderService readerService;

    @ApiOperation(value = "检查学号是否存在", notes = "检查学号")
    @GetMapping("/checkCertId")
    public boolean checkCertId(@RequestParam("certId") String certId) {
        return readerService.checkCertId(certId);
    }
}
