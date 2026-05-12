package com.hytc.recommendation_data.library.entity.extend;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hytc.recommendation_data.library.entity.Collect;
import com.hytc.recommendation_data.library.entity.Lend;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: White Jiang
 * @Date: 2023/2/22 14:43
 * @Description: 借阅多表连接类
 */
@Getter
@Setter
@ToString(callSuper = true)
@TableName("recommend")
public class LendExtend extends Lend {
    @TableField(exist = false)
    private Collect collect;
}
