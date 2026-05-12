package com.hytc.recommendation_data.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hytc.recommendation_data.common.util.PageUtils;
import com.hytc.recommendation_data.library.entity.Book;
import com.hytc.recommendation_data.library.entity.Lend;
import com.hytc.recommendation_data.library.entity.Reader;
import com.hytc.recommendation_data.library.mapper.BookMapper;
import com.hytc.recommendation_data.library.mapper.LendMapper;
import com.hytc.recommendation_data.library.mapper.ReaderMapper;
import com.hytc.recommendation_data.library.model.LendQueryDTO;
import com.hytc.recommendation_data.library.service.ILendService;
import com.hytc.recommendation_data.sys.entity.User;
import com.hytc.recommendation_data.sys.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author White Jiang
 * @since 2023-01-29
 */
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements ILendService {
    @Autowired
    BookMapper bookMapper;
    @Autowired
    ReaderMapper readerMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public PageUtils queryPage(LendQueryDTO queryDTO){
        QueryWrapper<Lend>wrapper = new QueryWrapper<Lend>().like(
                StringUtils.isNotEmpty(queryDTO.getCertId()),
                "CERT_ID",queryDTO.getCertId());
        wrapper.orderByAsc("LEND_DATE");
        Page<Lend> page = this.page(queryDTO.page(),wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Lend> queryLends(String certId) {
        QueryWrapper<Lend>wrapper = new QueryWrapper<Lend>();
        wrapper.eq("CERT_ID",certId);
        wrapper.orderByAsc("LEND_DATE");
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public void saveLend(String callNo, String account) {
        QueryWrapper<Book> bookQueryWrapper = new QueryWrapper<Book>()
                .eq("M_CALL_NO",callNo);
        Book book = bookMapper.selectOne(bookQueryWrapper);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>()
                .eq("account",account);
        User user = userMapper.selectOne(userQueryWrapper);
        QueryWrapper<Reader> readerQueryWrapper = new QueryWrapper<Reader>()
                .eq("CERT_ID",user.getCertId());
        Reader reader = readerMapper.selectOne(readerQueryWrapper);
        Lend lend= new Lend();
        lend.setCertId(reader.getCertId());
        lend.setName(reader.getName());
        lend.setmCallNo(book.getmCallNo());
        lend.setmTitle(book.getmTitle());
        lend.setmAuthor(book.getmAuthor());
        lend.setmPublisher(book.getmPublisher());
        lend.setLendDate(LocalDate.now().toString());
        this.save(lend);
    }

    @Override
    public Lend queryLend(String callNo,String account) {
        QueryWrapper<User> bookQueryWrapper = new QueryWrapper<User>().eq("account",account);
        User user = userMapper.selectOne(bookQueryWrapper);
        String certId = user.getCertId();
        QueryWrapper<Lend> wrapper = new QueryWrapper<Lend>();
        wrapper.eq("CERT_ID",certId);
        wrapper.eq("M_CALL_NO",callNo);
        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public void deleteLend(String certId,String callNo) {
        QueryWrapper<Lend> wrapper = new QueryWrapper<Lend>();
        wrapper.eq("CERT_ID",certId);
        wrapper.eq("M_CALL_NO",callNo);
        this.baseMapper.delete(wrapper);
    }
}
