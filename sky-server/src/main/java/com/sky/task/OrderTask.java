package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
* 订单定时任务
* */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
//    @Scheduled(cron = "2/5 * * * * ?")
    public void processTimeoutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //select * from order where status = 1 and order_time < (当前时间 - 15分钟)
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().minusMinutes(15));

        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时，取消订单");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?")//每日凌晨1点触发一次
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理派送中订单：{}", LocalDateTime.now());
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().minusMinutes(60));
        if (ordersList != null && ordersList.size() > 0) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
