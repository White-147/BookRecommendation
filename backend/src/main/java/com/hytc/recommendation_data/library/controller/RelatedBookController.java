package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.library.entity.extend.RelatedBookExtend;
import com.hytc.recommendation_data.library.service.IRelatedBookExtendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-03-01
 */
@Api(tags = "相似图书信息", value = "RelatedBook")
@RestController
@RequestMapping("/library/relatedBook")
public class RelatedBookController {
    @Autowired
    private IRelatedBookExtendService relatedBookExtendService;

    @ApiOperation(value = "查询相关图书", notes = "相关信息")
    @GetMapping("/getRelatedBook")
    public List<RelatedBookExtend> getRelatedBook(@RequestParam("callNo") String callNo,
                                                  @RequestParam("certId") String certId) {
        return relatedBookExtendService.queryRelatedBook(certId, callNo);
    }
}
