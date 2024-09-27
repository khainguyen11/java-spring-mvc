package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ProductController {

    private final ProductService productService;
    private final UploadService uploadService;

    public ProductController(ProductService productService, UploadService uploadService) {
        this.productService = productService;
        this.uploadService = uploadService;
    }

    // uplate product
    @GetMapping("/admin/product/update/{id}")
    public String getUpdatePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Product currenProduct = this.productService.getProductById(id);
        model.addAttribute("newProduct", currenProduct);
        return "admin/product/update";
    }

    @PostMapping("/admin/product/update")
    public String postUpdatePage(Model model, @ModelAttribute("newProduct") Product updateProduct,
            @RequestParam("productFile") MultipartFile file) {
        Product currentProduct = this.productService.getProductById(updateProduct.getId());
        if (currentProduct != null) {
            currentProduct.setDetailDesc(updateProduct.getDetailDesc());
            currentProduct.setFactory(updateProduct.getFactory());
            currentProduct.setName(updateProduct.getName());
            currentProduct.setShortDesc(updateProduct.getShortDesc());
            currentProduct.setQuantity(updateProduct.getQuantity());
            currentProduct.setTarget(updateProduct.getTarget());
            currentProduct.setPrice(updateProduct.getPrice());
            if (!file.isEmpty()) {
                this.uploadService.handleDeleteAvatar("product", currentProduct.getImage());
                String image = this.uploadService.handleSaveUploadFile(file, "product");
                currentProduct.setImage(image);

            }
            this.productService.handleSaveProduct(currentProduct);
        }
        return "redirect:admin/product";
    }

    // delete product
    @GetMapping("/admin/product/delete/{id}")
    public String getDeletePage(Model model, @PathVariable long id) {
        model.addAttribute(id);
        model.addAttribute("newProduct", new Product());
        return "admin/product/delete";
    }

    @PostMapping("/admin/product/delete")
    public String postDeleteProduct(Model model, @ModelAttribute Product newProduct) {
        this.productService.handelDeleteProduct(newProduct.getId());

        // logic delete images
        return "redirect:admin/product";
    }

    @GetMapping("/admin/product/{id}")
    public String getMethodName(Model model, @PathVariable long id) {
        model.addAttribute(id);
        Product currentProduct = this.productService.getProductById(id);
        model.addAttribute("product", currentProduct);
        return "admin/product/detail";
    }

    @GetMapping("/admin/product")
    public String getProduct(Model model) {
        List<Product> products = this.productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/product/show";
    }

    @GetMapping("/admin/product/create")
    public String getCreateProduct(Model model) {
        model.addAttribute("newProduct", new Product());
        return "admin/product/create";
    }

    @PostMapping("/admin/product/create")
    public String postMethodName(@ModelAttribute("newProduct") @Valid Product newProduct,
            BindingResult newProductBindingResult,
            @RequestParam("productFile") MultipartFile file) {
        // validate

        List<FieldError> errors = newProductBindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(error);
            System.out.println(error.getField() + "_" + error.getDefaultMessage());
        }
        //
        if (newProductBindingResult.hasErrors()) {
            return "admin/product/create";
        }

        String product = this.uploadService.handleSaveUploadFile(file, "product");
        if (product.charAt(product.length() - 1) != '-') {
            newProduct.setImage(product);
        }
        this.productService.handleSaveProduct(newProduct);
        return "redirect:admin/product";
    }

}
