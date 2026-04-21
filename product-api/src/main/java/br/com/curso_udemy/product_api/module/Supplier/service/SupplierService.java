package br.com.curso_udemy.product_api.module.Supplier.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.module.Supplier.model.Supplier;
import br.com.curso_udemy.product_api.module.Supplier.repository.SupplierRepository;
import br.com.curso_udemy.product_api.module.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class SupplierService {
    private static final  String  NO_SUPPLIER_ID = "There are no supplier for the given ID.";
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<SupplierResponse> findAll(){
        return supplierRepository.findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }

    public Supplier findById(Integer id){
        validateInformedId(id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ValidationException(NO_SUPPLIER_ID));
    }

    public List<SupplierResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("the supplier name must be informed");
        }
        return supplierRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse save(SupplierRequest request){
        validateSupplierNameInformed(request);
        var supplier =  supplierRepository.save(Supplier.of(request));
        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request,Integer id){
        validateSupplierNameInformed(request);
        validateInformedId(id);
        if(!supplierRepository.existsById(id)){
            throw new ValidationException(NO_SUPPLIER_ID);
        }
        var supplier = Supplier.of(request);
        supplier.setId(id);
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }

    private void validateSupplierNameInformed(SupplierRequest request){
        if(isEmpty(request.getName())){
            throw new ValidationException("the supplier's name was no informed");
        }
    }

    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        if(productRepository.existsBySupplierId(id)){
            throw new ValidationException("You cannot delete this supplier because it's already defined by a product.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("the supplier was deleted.");

    }
    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("the supplier id was not informed.");
        }
    }


}
