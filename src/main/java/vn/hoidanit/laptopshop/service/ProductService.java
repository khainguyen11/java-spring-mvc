package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.repository.Specification.ProductSpecs;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, CartRepository cartRepository,
            CartDetailRepository cartDetailRepository, UserService userService, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
    }

    public void handelDeleteProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public Product handleSaveProduct(Product product) {
        return this.productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    // private Specification<Product> NameLike(String name) {
    // return (root, query, criteriaBuilder) ->
    // criteriaBuilder.like(root.get(Product_.NAME), "%" + name + "%");

    // }

    // public Specification<Product> queryByName(String name){
    // return (root,query,builder) -> {

    // }
    // }

    public Page<Product> fetchProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public Page<Product> fetchProductswihSpec(Pageable pageable, ProductCriteriaDTO productCriteriaDTO) {
        if (productCriteriaDTO.getTarget() == null && productCriteriaDTO.getFactory() == null
                && productCriteriaDTO.getPrice() == null) {
            return this.productRepository.findAll(pageable);
        }
        Specification<Product> combinedSpec = Specification.where(null);
        if (productCriteriaDTO.getTarget() != null && productCriteriaDTO.getTarget().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListTarget(productCriteriaDTO.getTarget().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }
        if (productCriteriaDTO.getFactory() != null && productCriteriaDTO.getFactory().isPresent()) {
            Specification<Product> currentSpecs = ProductSpecs.matchListFactory(productCriteriaDTO.getFactory().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }
        if (productCriteriaDTO.getPrice() != null && productCriteriaDTO.getPrice().isPresent()) {
            Specification<Product> currentSpecs = this.buildPriceSpecification(productCriteriaDTO.getPrice().get());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        return this.productRepository.findAll(combinedSpec, pageable);
    }

    // case 1

    // public Page<Product> fetchProductsWithSpec(Pageable pageable, double min) {
    // return this.productRepository.findAll(ProductSpecs.minPrice(min), pageable);
    // }

    // case 2
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, double max) {
    // return this.productRepository.findAll(ProductSpecs.maxPrice(max), pageable);
    // }

    // case 3
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, String factory)
    // {
    // return this.productRepository.findAll(ProductSpecs.matchFactory(factory),
    // pageable);
    // }
    // case 4
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, List<String>
    // factory) {
    // return this.productRepository.findAll(ProductSpecs.matchListFactory(factory),
    // pageable);
    // }
    // case5
    // public Page<Product> fetchProductsWithSpec(Pageable pageable, String price) {
    // // eg 10-15
    // if (price.equals("10-toi-15-trieu")) {
    // double min = 10000000;
    // double max = 15000000;
    // return this.productRepository.findAll(ProductSpecs.matchPrice(min, max),
    // pageable);
    // } else if (price.equals("15-toi-30-trieu")) {
    // double min = 15000000;
    // double max = 30000000;
    // return this.productRepository.findAll(ProductSpecs.matchPrice(min, max),
    // pageable);
    // } else
    // return this.productRepository.findAll(pageable);
    // }
    public Specification<Product> buildPriceSpecification(List<String> price) {
        Specification<Product> combinedSpec = Specification.where(null);
        for (String p : price) {
            double min = 0;
            double max = 0;
            switch (p) {
                case "duoi-10-trieu":
                    min = 10000000;
                    break;
                case "10-15-trieu":
                    min = 10000000;
                    max = 15000000;
                    break;
                case "15-20-trieu":
                    min = 15000000;
                    max = 20000000;
                    break;
                case "tren-20-triệu":
                    min = 20000000;
                    max = 300000000;
                    break;
            }
            if (min != 0 && max != 0) {
                Specification<Product> rangeSpec = ProductSpecs.matchMultiplePrice(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            }
        }
        return combinedSpec;
    }

    public int handleCountProduct() {
        int countProduct = 0;
        List<Product> products = this.productRepository.findAll();
        for (Product product : products) {
            countProduct++;

        }
        return countProduct;
    }

    public List<CartDetail> getAllProductFromCart() {
        return this.cartDetailRepository.findAll();
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id);
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUser(user);
    }

    public void handleRemoveCart(long id, HttpSession session) {
        Optional<CartDetail> cartDetail = this.cartDetailRepository.findById(id);

        if (cartDetail.isPresent()) {
            CartDetail currentCartDetail = cartDetail.get();
            Cart currentCart = currentCartDetail.getCart();

            this.cartDetailRepository.deleteById(id);
            // update Cart
            if (currentCart.getSum() > 1) {
                int s = currentCart.getSum() - 1;
                currentCart.setSum(s);
                session.setAttribute("sum", s);
                this.cartRepository.save(currentCart);
            } else {
                this.cartRepository.deleteById(currentCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
        for (CartDetail cartDetail : cartDetails) {
            Optional<CartDetail> cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
            if (cdOptional.isPresent()) {
                CartDetail currentCartDetail = cdOptional.get();
                currentCartDetail.setQuantity(cartDetail.getQuantity());
                this.cartDetailRepository.save(currentCartDetail);
            }
        }
    }

    public void handlePlaceOrder(User user, HttpSession session, String receiverName, String receiverAddress,
            String receiverPhone) {
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setStatus("PENDING");

        order = this.orderRepository.save(order);
        // create OrderDetail
        Cart cart = this.cartRepository.findByUser(user);
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();
            if (cartDetails != null) {

                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProduct(cd.getProduct());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());
                    this.orderDetailRepository.save(orderDetail);
                    order.setTotalPrice(order.getTotalPrice() + orderDetail.getPrice() * orderDetail.getQuantity());

                }
                // xoa cart
                this.orderRepository.save(order);
                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.deleteById(cd.getId());
                }
                this.cartRepository.deleteById(cart.getId());
                // update ss
                session.setAttribute("sum", 0);
            }

        }
    }

    public void handleAddProductToCart(String email, long productId, HttpSession session, int updateQuantity) {
        // check user ddax co ccart chua
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);

            if (cart == null) {
                // create cart
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setSum(0);

                cart = this.cartRepository.save(newCart);
            }

            // save
            // tifm
            Product p = this.productRepository.findById(productId);
            if (p != null) {
                // check san pham da tung dc them trc day chua
                CartDetail oldP = this.cartDetailRepository.findByCartAndProduct(cart, p);
                if (oldP == null) {
                    CartDetail cd = new CartDetail();
                    cd.setCart(cart);
                    cd.setProduct(p);
                    cd.setPrice(p.getPrice());
                    cd.setQuantity(1);
                    this.cartDetailRepository.save(cd);
                    int s = cart.getSum() + 1;
                    cart.setSum(s);
                    this.cartRepository.save(cart);
                    session.setAttribute("sum", s);
                } else {
                    oldP.setQuantity(oldP.getQuantity() + updateQuantity);
                    this.cartDetailRepository.save(oldP);
                }

            }

        }

    }
}
