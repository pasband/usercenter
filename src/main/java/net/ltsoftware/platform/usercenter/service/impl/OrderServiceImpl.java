package net.ltsoftware.platform.usercenter.service.impl;

import net.ltsoftware.platform.dao.OrderMapper;
import net.ltsoftware.platform.model.Order;
import net.ltsoftware.platform.service.OrderService;
import net.ltsoftware.platform.usercenter.dao.OrderMapper;
import net.ltsoftware.platform.usercenter.model.Order;
import net.ltsoftware.platform.usercenter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
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

}
