package vn.hoidanit.laptopshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.OrderService;
import java.util.List;
import java.util.Optional;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getOrder(Model model) {

        List<Order> orders = this.orderService.getAllOrder();
        model.addAttribute("orders", orders);

        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getOrderDetail(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Optional<Order> currentOrder = this.orderService.getOrderById(id);
        if (currentOrder.isPresent()) {
            model.addAttribute("orderDetails", currentOrder.get().getOrderDetail());

        }
        return "admin/order/detail";
    }

    @GetMapping("/admin/order/update/{id}")
    public String getOrderUpdatePage(Model model, @PathVariable long id) {
        Optional<Order> currentOrder = this.orderService.getOrderById(id);
        if (currentOrder.isPresent()) {
            model.addAttribute("newOrder", currentOrder.get());

        }
        return "admin/order/update";
    }

    @PostMapping("/admin/order/update")
    public String updateOrderStatus(Model model, @ModelAttribute("newOrder") Order order) {

        Optional<Order> currentOrder = this.orderService.getOrderById(order.getId());
        if (currentOrder.isPresent()) {
            currentOrder.get().setStatus(order.getStatus());
            this.orderService.handleSaveOrder(currentOrder.get());
        }

        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String getOrderDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Optional<Order> currentOrder = this.orderService.getOrderById(id);
        if (currentOrder.isPresent()) {
            model.addAttribute("newOrder", currentOrder.get());

        }
        return "admin/order/delete";
    }

    @PostMapping("/admin/order/delete")
    public String deleteOrder(Model model, @ModelAttribute("newOrder") Order order) {

        Optional<Order> currentOrder = this.orderService.getOrderById(order.getId());
        if (currentOrder.isPresent()) {
            List<OrderDetail> orderDetails = currentOrder.get().getOrderDetail();
            for (OrderDetail cd : orderDetails) {
                this.orderService.handleDeleteOrderDetail(cd.getId());
            }
            this.orderService.handleDeleteOrder(order.getId());
        }

        return "redirect:/admin/order";
    }

    @GetMapping("/order-history")
    public String getOrderHistory(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = new User();
        long id = (long) session.getAttribute("id");
        user.setId(id);
        List<Order> orders = this.orderService.handleHistoryOrder(user);
        model.addAttribute("orders", orders);
        return "client/order/history";
    }

}
