package br.com.curso_udemy.product_api.module.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockDTO {

    private String salesId;

   private List<ProductQuantityDTO> products;

}
