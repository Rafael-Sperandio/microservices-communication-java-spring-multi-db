package br.com.curso_udemy.product_api.module.Supplier.controller;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.module.Supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<SupplierResponse> findAll() {
        return supplierService.findAll();
    }

    @GetMapping("{id}")
    public SupplierResponse findById(@PathVariable Integer id) {
        return supplierService.findByIdResponse(id);
    }

    @GetMapping("name/{name}")
    public List<SupplierResponse> findByDescription(@PathVariable String name) {
        return supplierService.findByName(name);
    }
    @PostMapping
    public SupplierResponse save(@RequestBody SupplierRequest request){
        return supplierService.save(request);
    }


    @PutMapping("{id}")
    public SupplierResponse update(@RequestBody SupplierRequest request , @PathVariable Integer id) {
        return supplierService.update(request,id);
    }
    @DeleteMapping("{id}")
    public SuccessResponse delete(@PathVariable Integer id) {
        return supplierService.delete(id);
    }
}
