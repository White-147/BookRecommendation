package com.hytc.recommendation_data.library.entity.extend;

import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.entity.Collect;
import com.hytc.recommendation_data.library.entity.Reader;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: White Jiang
 * @Date: 2023/2/10 9:44
 * @Description: 收藏多表连接类
 */
@Getter
@Setter
public class CollectExtend extends Collect {
    private Reader reader;

    private Book book;
}
