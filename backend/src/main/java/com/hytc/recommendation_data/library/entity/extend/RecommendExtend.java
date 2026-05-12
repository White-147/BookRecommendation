package com.hytc.recommendation_data.library.entity.extend;

import com.hytc.recommendation_data.library.entity.Recommend;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: White Jiang
 * @Date: 2023/2/22 13:29
 * @Description: 推荐多表连接类
 */
@Setter
@Getter
public class RecommendExtend extends Recommend {
    private BookExtend bookExtend;
}
