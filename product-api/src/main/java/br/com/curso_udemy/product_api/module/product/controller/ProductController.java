package br.com.curso_udemy.product_api.module.product.controller;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.module.product.dto.ProductCheckStockRequest;
import br.com.curso_udemy.product_api.module.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.module.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.module.product.dto.ProductSalesResponse;
import br.com.curso_udemy.product_api.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("{id}")
    public ProductResponse findById(@PathVariable Integer id) {
        return productService.findByIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<ProductResponse> findByName(@PathVariable String name) {
        return productService.findByName(name);
    }

    @GetMapping("category/{categoryId}")
    public List<ProductResponse> findByCategoryId(@PathVariable Integer categoryId) {
        return productService.findByCategoryId(categoryId);
    }
    @GetMapping("supplier/{supplierId}")
    public List<ProductResponse> findBySupplierId(@PathVariable Integer supplierId) {
        return productService.findBySupplierId(supplierId);
    }

    @PostMapping
    public ProductResponse save(@RequestBody ProductRequest request){

        return productService.save(request);
    }
    @PutMapping("{id}")
    public ProductResponse update(@RequestBody ProductRequest request , @PathVariable Integer id) {
        return productService.update(request,id);
    }

    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return productService.delete(id);
    }

    @PostMapping("check-stock")
    public SuccessResponse checkProductsStock(@RequestBody ProductCheckStockRequest request) {
        return productService.checkProductsStock(request);
    }

    @GetMapping("{id}/sales/")
    public ProductSalesResponse findProductSales(@PathVariable Integer id){
        return  productService.findProductSales(id);
    }


}
