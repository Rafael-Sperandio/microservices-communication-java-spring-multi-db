package br.com.curso_udemy.product_api.module.product.service;

import br.com.curso_udemy.product_api.config.exceptions.SuccessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.module.Supplier.service.SupplierService;
import br.com.curso_udemy.product_api.module.category.service.CategoryService;
import br.com.curso_udemy.product_api.module.product.dto.*;
import br.com.curso_udemy.product_api.module.product.model.Product;
import br.com.curso_udemy.product_api.module.product.repository.ProductRepository;
import br.com.curso_udemy.product_api.module.sales.client.SalesClient;
import br.com.curso_udemy.product_api.module.sales.dto.SalesConfirmationDTO;
import br.com.curso_udemy.product_api.module.sales.enums.SalesStatus;
import br.com.curso_udemy.product_api.module.sales.rabbitmq.SalesConfirmationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
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

    @Autowired
    private SalesConfirmationSender salesConfirmationSender;

    @Autowired
    private SalesClient salesClient;

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
            throw new ValidationException("the quantity Should not be  less or equal than 0");
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

    public SuccessResponse delete(Integer id){
        validateInformedId(id);
        productRepository.deleteById(id);
        return SuccessResponse.create("the product was deleted.");

    }
    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("the product id was not informed.");
        }
    }

    public  void updateProductStock(ProductStockDTO product){

        try {
            validateStockUpdateData(product);
            updateStock(product);
        }catch (Exception ex){
            log.error("Error while trying to update stock for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED);
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    @Transactional
    private void updateStock(ProductStockDTO product) {
        var productsForUpdate = new ArrayList<Product>();
        product.getProducts().forEach(salesProduct ->{
            var existingProduct = findById(salesProduct.getProductId());

            validateQuantityInStock(salesProduct,existingProduct);
            existingProduct.updateStock(salesProduct.getQuantity());
            productsForUpdate.add(existingProduct);
        });

        if (!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED);
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }
    private void validateStockUpdateData(ProductStockDTO product){
        if(isEmpty(product) || isEmpty(product.getSalesId())){
            throw new ValidationException("The product data or sales ID cannot be null.");
        }
        if(isEmpty(product.getProducts())){
            throw  new ValidationException("The sales Product must be informed.");
        }
        product.getProducts()
                .forEach(salesProduct -> {
                    //TODO: validação pode ser transforamda em metodo
                    if(isEmpty(salesProduct.getQuantity())
                           || isEmpty(salesProduct.getProductId())){
                        throw  new ValidationException("The product id and quantity must be informed.");
                    }
                    if(salesProduct.getQuantity()<1){
                        throw  new ValidationException("the quantity Should not be  less or equal than 0");
                    }
                });

    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct,
                                         Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("The product %s is out of stock.", existingProduct.getId()));
        }
    }

    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        try {
            var sales = salesClient.findSalesByProductId(product.getId())
                            .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            return ProductSalesResponse.of(product,sales.getSalesIds());
        }catch (Exception ex){
            throw new ValidationException("there was an error while trying to get product's sales.");
        }
    }

    public SuccessResponse checkProductsStock(ProductCheckStockRequest request){
        if(isEmpty(request) || isEmpty(request.getProducts())){
            throw new ValidationException("the request data and products must be informed");
        }

        request
                .getProducts()
                .forEach(this::validateStock);
        return SuccessResponse.create("The stock is ok!");
    }

    private void validateStock(ProductQuantityDTO productQuantity) {
        if (isEmpty(productQuantity.getProductId()) || isEmpty(productQuantity.getQuantity())) {
            throw new ValidationException("Product ID and quantity must be informed.");
        }
        var product = findById(productQuantity.getProductId());
        validateQuantityInStock(productQuantity,product);
    }


}
