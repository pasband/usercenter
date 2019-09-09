package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.dao.TradeMapper;
import net.ltsoftware.usercenter.model.Trade;
import net.ltsoftware.usercenter.model.TradeExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TradeService implements BaseService<Trade, TradeExample> {

    @Resource
    private TradeMapper tradeMapper;

    @Override
    public Trade selectByPrimaryKey(Long key) throws Exception {
        return tradeMapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer updateByPrimaryKey(Trade Trade) throws Exception {
        return tradeMapper.updateByPrimaryKey(Trade);
    }

    @Override
    @Transactional
    public Integer deleteByPrimaryKey(Long key) throws Exception {
        return tradeMapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer insert(Trade Trade) throws Exception {
        return tradeMapper.insert(Trade);
    }

    @Override
    public List<Trade> selectByExample(TradeExample TradeExample) throws Exception {
        return tradeMapper.selectByExample(TradeExample);
    }

    public Trade selectByTradeNo(String trade_no) {
        Trade Trade = null;

        TradeExample example = new TradeExample();
        example.createCriteria().andTradeNoEqualTo(trade_no);
        List<Trade> list = tradeMapper.selectByExample(example);

        if (list != null && list.size() == 1) {
            Trade = list.get(0);
        }
        return Trade;
    }
}
