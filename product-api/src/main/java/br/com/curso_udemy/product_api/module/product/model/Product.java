package br.com.curso_udemy.product_api.module.product.model;

import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.module.Supplier.model.Supplier;
import br.com.curso_udemy.product_api.module.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.module.category.model.Category;
import br.com.curso_udemy.product_api.module.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.module.product.dto.ProductResponse;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 1 produto possui 1 categoria e 1 fornecedor
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="PRODUCT")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    @Column(name = "NAME",nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "FK_SUPPLIER")
    private Supplier supplier;


    @ManyToOne
    @JoinColumn(name = "FK_CATEGORY")
    private Category category;

    @Column(name = "QUANTITY_AVAILABLE",nullable = false)
    private Integer quantityAvailable;

    @Column(name = "CREATED_AT",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
    }

    public static Product of(ProductRequest request,Supplier supplier,Category category){
        return Product
                .builder()
                .name(request.getName())
                .quantityAvailable(request.getQuantityAvailable())
                .supplier(supplier)
                .category(category)
                .build();
    }

    //remove uma quantidade de produtos do stock
    public void updateStock(Integer quantity){
        this.quantityAvailable -= quantity;
    }
}
