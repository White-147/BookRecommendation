package com.hytc.recommendation_data.library.entity.extend;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hytc.recommendation_data.library.entity.Newbook;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: White Jiang
 * @Date: 2023/3/2 17:11
 * @Description:
 */
@Setter
@Getter
@ToString(callSuper = true)
@TableName("newbook")
public class NewbookExtend extends Newbook {
    @TableField(exist = false)
    private BookExtend bookExtend;
}
