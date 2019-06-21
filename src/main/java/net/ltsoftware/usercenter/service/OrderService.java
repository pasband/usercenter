package net.ltsoftware.usercenter.service;

import net.ltsoftware.usercenter.model.Order;
import net.ltsoftware.usercenter.model.OrderExample;

public interface OrderService extends BaseService<Order, OrderExample> {

    Order selectByTradeNo(String trade_no);
}
