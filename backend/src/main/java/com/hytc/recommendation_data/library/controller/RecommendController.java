package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.model.RecommendDTO;
import com.hytc.recommendation_data.library.service.IRecommendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-02-22
 */
@Api(tags = "推荐信息", value = "Recommend")
@RestController
@RequestMapping("/library/recommend")
public class RecommendController {
    @Autowired
    private IRecommendService recommendService;

    @ApiOperation(value = "查询推荐图书", notes = "推荐信息")
    @GetMapping("/getRecommend")
    public PageUtils getRecommend(@ApiParam("查询的条件") RecommendDTO queryDTO,
                                  @RequestParam("pageSize") int pageSize,
                                  @RequestParam("currentPage") int currentPage) {
        return recommendService.getRecommend(queryDTO, pageSize, currentPage);
    }
}