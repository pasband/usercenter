package net.ltsoftware.platform.usercenter.service;

import net.ltsoftware.platform.usercenter.model.Order;
import net.ltsoftware.platform.usercenter.model.OrderExample;

public interface OrderService extends BaseService<Order, OrderExample> {

    public Order selectByTradeNo(String trade_no);
}
