package com.hytc.recommendation_data.library.entity.extend;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hytc.recommendation_data.library.entity.Collect;
import com.hytc.recommendation_data.library.entity.RelatedBook;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: White Jiang
 * @Date: 2023/3/1 10:56
 * @Description: 相似图书多表连接类
 */
@Getter
@Setter
@ToString(callSuper = true)
@TableName("relatedbook")
public class RelatedBookExtend extends RelatedBook {
    @TableField(exist = false)
    private BookExtend bookExtend;

    @TableField(exist = false)
    private Collect collect;
}
