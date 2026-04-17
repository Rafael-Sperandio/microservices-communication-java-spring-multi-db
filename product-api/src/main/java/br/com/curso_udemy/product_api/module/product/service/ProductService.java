package br.com.curso_udemy.product_api.module.product.service;

import br.com.curso_udemy.product_api.config.exceptions.SucessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.module.Supplier.service.SupplierService;
import br.com.curso_udemy.product_api.module.category.service.CategoryService;
import br.com.curso_udemy.product_api.module.product.dto.ProductRequest;
import br.com.curso_udemy.product_api.module.product.dto.ProductResponse;
import br.com.curso_udemy.product_api.module.product.model.Product;
import br.com.curso_udemy.product_api.module.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class ProductService {

    private static final String NO_PRODUCT_ID = "There are no product for the given ID.";
    private static  final Integer ZERO = 0;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;

    public List<ProductResponse> findAll(){
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id){
        validateInformedId(id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ValidationException(NO_PRODUCT_ID));
    }


    public List<ProductResponse> findByName(String name){
        if(isEmpty(name)){
            throw new ValidationException("the product name must be informed");
        }
        return productRepository.findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId){
        if(isEmpty(categoryId)){
            throw new ValidationException("the product's category Id must be informed");
        }
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId){
        if(isEmpty(supplierId)){
            throw new ValidationException("the product's supplier Id must be informed");
        }
        return productRepository.findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public  boolean existByCategoryId(Integer categoryId){
        return  productRepository.existsByCategoryId(categoryId);
    }
    public boolean existBySupplierId(Integer supplierId){
        return productRepository.existsBySupplierId(supplierId);
    }

    public ProductResponse save(ProductRequest request){
        validateProductDataInformed(request);
        validateCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product =  productRepository.save(Product.of(request,supplier,category));
        return ProductResponse.of(product);
    }

    public ProductResponse update(ProductRequest request, Integer id){
        validateProductDataInformed(request);
        validateInformedId(id);
        if(!productRepository.existsById(id)){
            throw new ValidationException(NO_PRODUCT_ID);
        }
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product =  Product.of(request,supplier,category);
        product.setId(id);
        productRepository.save(product);
        return ProductResponse.of(product);
    }

    private void validateProductDataInformed(ProductRequest request) {
        if (isEmpty(request.getName())) {
            throw new ValidationException("the Product's name was no informed");
        }
        if(isEmpty(request.getQuantityAvailable())){
            throw new ValidationException("the Product's quantity was no informed");
        } else if (request.getQuantityAvailable() <= ZERO ) {
            throw new ValidationException("the quantity Should be no less or equal than 0");
        }
    }
    private void  validateCategoryAndSupplierIdInformed(ProductRequest request){
        if(isEmpty(request.getCategoryId())){
            throw new ValidationException("the Category Id was not informed");
        }

        if(isEmpty(request.getSupplierId())){
            throw new ValidationException("the Supplier Id was not informed");
        }
    }

    public SucessResponse delete(Integer id){
        validateInformedId(id);
        productRepository.deleteById(id);
        return SucessResponse.create("the product was deleted.");

    }
    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("the product id was not informed.");
        }
    }

}
