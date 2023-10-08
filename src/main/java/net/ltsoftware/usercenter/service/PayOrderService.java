package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.dao.PayOrderMapper;
import net.ltsoftware.usercenter.model.PayOrder;
import net.ltsoftware.usercenter.model.PayOrderExample;
import net.ltsoftware.usercenter.model.Trade;
import net.ltsoftware.usercenter.model.TradeExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PayOrderService implements BaseService<PayOrder, PayOrderExample> {

    @Resource
    private PayOrderMapper payOrderMapper;

    @Override
    public PayOrder selectByPrimaryKey(Long key)  {
        return payOrderMapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer updateByPrimaryKey(PayOrder payOrder)  {
        return payOrderMapper.updateByPrimaryKey(payOrder);
    }

    @Override
    @Transactional
    public Integer deleteByPrimaryKey(Long key)  {
        return payOrderMapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer insert(PayOrder payOrder) {
        return payOrderMapper.insert(payOrder);
    }

    @Override
    public List<PayOrder> selectByExample(PayOrderExample payOrderExample) {
        return payOrderMapper.selectByExample(payOrderExample);
    }

    public PayOrder selectByMchOrderNo(String mchOrderNo) {
        PayOrder payOrder = null;

        PayOrderExample example = new PayOrderExample();
        example.createCriteria().andMchOrderNoEqualTo(mchOrderNo);
        List<PayOrder> list = payOrderMapper.selectByExampleWithBLOBs(example);

        if (list != null && list.size() == 1) {
            payOrder = list.get(0);
        }
        return payOrder;
    }
}
