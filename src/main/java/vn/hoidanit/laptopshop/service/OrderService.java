package vn.hoidanit.laptopshop.service;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public int handleCountOrder() {
        int countOrder = 0;
        List<Order> orders = this.orderRepository.findAll();
        for (Order order : orders) {
            countOrder++;
        }
        return countOrder;
    }

    public List<Order> getAllOrder() {
        return this.orderRepository.findAll();
    }

    public Optional<Order> getOrderById(long id) {
        return this.orderRepository.findById(id);
    }

    public void handleSaveOrder(Order order) {
        this.orderRepository.save(order);
    }

    public void handleDeleteOrder(long id) {
        this.orderRepository.deleteById(id);
    }

    public void handleDeleteOrderDetail(long id) {
        this.orderDetailRepository.deleteById(id);
    }

    public List<Order> handleHistoryOrder(User user) {
        return this.orderRepository.findByUser(user);
    }
}
