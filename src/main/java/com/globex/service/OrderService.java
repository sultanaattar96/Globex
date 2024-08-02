package com.globex.service;

import com.globex.model.Order;
import com.globex.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void placeOrder(Order order) {
        order.setOrderDate(new Date());
        order.setDelivered(true); // Assume order is delivered immediately for simplicity
        orderRepository.save(order);
    }
}
