package net.ltsoftware.platform.usercenter.service.impl;

import net.ltsoftware.platform.usercenter.dao.OrderMapper;
import net.ltsoftware.platform.usercenter.model.Order;
import net.ltsoftware.platform.usercenter.model.OrderExample;
import net.ltsoftware.platform.usercenter.service.OrderService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public Order selectByPrimaryKey(Long key) throws Exception {
        return orderMapper.selectByPrimaryKey(key);
    }

    @Override
    public Integer updateByPrimaryKey(Order order) throws Exception {
        return orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public Integer deleteByPrimaryKey(Long key) throws Exception {
        return orderMapper.deleteByPrimaryKey(key);
    }

    @Override
    public Integer insert(Order order) throws Exception {
        return orderMapper.insert(order);
    }

    @Override
    public List<Order> selectByExample(OrderExample orderExample) throws Exception {
        return orderMapper.selectByExample(orderExample);
    }

}
