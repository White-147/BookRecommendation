package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.model.NewBookExtendDTO;
import com.hytc.recommendation_data.library.service.INewbookExtendService;
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
 * @since 2023-03-02
 */
@Api(tags = "新书速递图书", value = "NewBook")
@RestController
@RequestMapping("/library/newBook")
public class NewBookController {
    @Autowired
    INewbookExtendService newbookExtendService;

    @ApiOperation(value = "查询分页新书速递派生", notes = "分页新书速递派生信息")
    @GetMapping("/queryNewBook")
    public PageUtils queryNewBook(@ApiParam("查询的条件") NewBookExtendDTO queryDTO,
                                  @RequestParam("pageSize") int pageSize,
                                  @RequestParam("currentPage") int currentPage) {

        return newbookExtendService.queryNewBook(queryDTO, pageSize, currentPage);
    }
}
