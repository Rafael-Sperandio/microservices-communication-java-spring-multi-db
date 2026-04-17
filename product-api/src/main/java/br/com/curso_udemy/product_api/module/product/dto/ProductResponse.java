package br.com.curso_udemy.product_api.module.product.dto;

import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.module.Supplier.model.Supplier;
import br.com.curso_udemy.product_api.module.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.module.category.model.Category;
import br.com.curso_udemy.product_api.module.product.model.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer id;
    private String name;
    private SupplierResponse supplier;
    private CategoryResponse category;
    @JsonProperty("quantity_available")
    private Integer quantityAvailable;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    public static ProductResponse of(Product product){
        return  ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .quantityAvailable(product.getQuantityAvailable())
                .createdAt(product.getCreatedAt())
                .supplier(SupplierResponse.of(product.getSupplier()))
                .category(CategoryResponse.of(product.getCategory()))
                .build();
    }

}
