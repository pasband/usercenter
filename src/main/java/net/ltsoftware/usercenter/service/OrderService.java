package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.dao.OrderMapper;
import net.ltsoftware.usercenter.model.Order;
import net.ltsoftware.usercenter.model.OrderExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderService implements BaseService<Order, OrderExample> {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public Order selectByPrimaryKey(Long key) throws Exception {
        return orderMapper.selectByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer updateByPrimaryKey(Order order) throws Exception {
        return orderMapper.updateByPrimaryKey(order);
    }

    @Override
    @Transactional
    public Integer deleteByPrimaryKey(Long key) throws Exception {
        return orderMapper.deleteByPrimaryKey(key);
    }

    @Override
    @Transactional
    public Integer insert(Order order) throws Exception {
        return orderMapper.insert(order);
    }

    @Override
    public List<Order> selectByExample(OrderExample orderExample) throws Exception {
        return orderMapper.selectByExample(orderExample);
    }

    public Order selectByTradeNo(String trade_no) {
        Order order = null;

        OrderExample example = new OrderExample();
        example.createCriteria().andTradeNoEqualTo(trade_no);
        List<Order> list = orderMapper.selectByExample(example);

        if (list != null && list.size() == 1) {
            order = list.get(0);
        }
        return order;
    }
}
