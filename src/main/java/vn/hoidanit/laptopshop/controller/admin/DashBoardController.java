package vn.hoidanit.laptopshop.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import vn.hoidanit.laptopshop.service.OrderService;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UserService;

@Controller
public class DashBoardController {
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;

    public DashBoardController(ProductService productService, OrderService orderService, UserService userService) {
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String getDashBoard(Model model) {
        int countUser = this.userService.handleCountUser();
        model.addAttribute("countUser", countUser);
        model.addAttribute("countProduct", this.productService.handleCountProduct());
        model.addAttribute("countOrder", this.orderService.handleCountOrder());
        return "admin/dashboard/show";
    }
}
