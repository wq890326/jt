package com.jt.store.order.dao;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.jt.common.vo.Where;
import com.jt.store.order.mapper.OrderMapper;
import com.jt.store.order.pojo.Order;
import com.jt.store.order.pojo.PageResult;
import com.jt.store.order.pojo.ResultMsg;

public class OrderDAO implements IOrder {

    @Autowired
    private OrderMapper orderMapper;

    public void createOrder(Order order) {
        this.orderMapper.save(order);
    }

    public Order queryOrderById(String orderId) {
        return this.orderMapper.queryByID(orderId);
    }

    public PageResult<Order> queryOrderByUserNameAndPage(String buyerNick, Integer page, Integer count) {
        PageBounds bounds = new PageBounds();
        bounds.setContainsTotalCount(true);
        bounds.setLimit(count);
        bounds.setPage(page);
        bounds.setOrders(com.github.miemiedev.mybatis.paginator.domain.Order.formString("create_time.desc"));
        PageList<Order> list = this.orderMapper
                .queryListByWhere(bounds, Where.build("buyer_nick", buyerNick));
        return new PageResult<Order>(list.getPaginator().getTotalCount(), list);
    }

    public ResultMsg changeOrderStatus(Order order) {
        try {
            order.setUpdateTime(new Date());
            this.orderMapper.update(order);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultMsg("500", "更新订单出错!");
        }
        return new ResultMsg("200", "更新成功!");
    }

}
