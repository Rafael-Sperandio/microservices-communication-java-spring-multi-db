package br.com.curso_udemy.product_api.module.sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesProductResponse {

    private List<String> salesIds;
}