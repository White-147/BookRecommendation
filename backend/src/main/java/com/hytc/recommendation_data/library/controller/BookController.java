package com.hytc.recommendation_data.library.controller;

import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.model.BookExtendDTO;
import com.hytc.recommendation_data.library.model.BookQueryDTO;
import com.hytc.recommendation_data.library.service.IBookExtendService;
import com.hytc.recommendation_data.library.service.IBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-29
 */
@Api(tags = "图书馆图书", value = "Book")
@RestController
@RequestMapping("/library/book")
public class BookController {
    @Autowired
    private IBookService bookService;

    @Autowired
    private IBookExtendService bookExtendService;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @ApiOperation(value = "查询分页图书", notes = "分页图书信息")
    @GetMapping("/list")
    public PageUtils list(@ApiParam("查询的条件") BookQueryDTO queryDTO) {
        return bookService.queryPage(queryDTO);
    }

    @ApiOperation(value = "查询分页图书派生", notes = "分页图书派生信息")
    @GetMapping("/queryBook")
    public PageUtils queryBook(@ApiParam("查询的条件") BookExtendDTO queryDTO,
                               @RequestParam("pageSize") int pageSize,
                               @RequestParam("currentPage") int currentPage) {
        return bookExtendService.queryBook(queryDTO, pageSize, currentPage);
    }

    @ApiOperation(value = "查询图书派生", notes = "图书信息")
    @GetMapping("/selectBookExtendByCallNo")
    public Book queryBookByCallNo(@RequestParam("callNo") String callNo,
                                  @RequestParam("certId") String certId) {
        String str = certId + "," + callNo + "," + "consultBook";
        kafkaTemplate.send("userLog", str);
        return bookExtendService.queryBookByCallNo(certId, callNo);
    }

    @ApiOperation(value = "查询图书", notes = "图书信息")
    @GetMapping("/selectByCallNo")
    public Book selectBookByCallId(@RequestParam("callNo") String callNo) {
        return bookService.queryBookByCallNo(callNo);
    }

    @ApiOperation(value = "更新图书信息", notes = "更新图书")
    @PostMapping("/save")
    public String update(@RequestBody Book book) {
        bookService.updateState(book);
        return "success";
    }
}
