package com.hytc.recommendation_data.library.entity.extend;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.entity.Collect;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: White Jiang
 * @Date: 2023/2/11 12:22
 * @Description: 图书多表连接类
 */
@Getter
@Setter
@ToString(callSuper = true)
@TableName("book")
public class BookExtend extends Book {
    @TableField(exist = false)
    private Collect collect;
}
