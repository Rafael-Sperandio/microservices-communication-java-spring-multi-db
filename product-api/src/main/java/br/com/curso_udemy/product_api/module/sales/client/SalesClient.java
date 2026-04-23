package br.com.curso_udemy.product_api.module.sales.client;


import br.com.curso_udemy.product_api.module.sales.dto.SalesProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Optional;


@FeignClient(name = "salesClient",contextId = "salesClient",
url = "${app-config.services.sales}")
public interface SalesClient {
    @GetMapping("/api/orders/product/{productId}")
    Optional<SalesProductResponse> findSalesByProductId(@PathVariable Integer productId);
    //@RequestHeader(name = "Authorization") String authorization,
    //@RequestHeader(name = "transactionId") String transactionId
}