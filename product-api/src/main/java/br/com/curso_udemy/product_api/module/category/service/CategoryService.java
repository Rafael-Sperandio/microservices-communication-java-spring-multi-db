package br.com.curso_udemy.product_api.module.category.service;
import br.com.curso_udemy.product_api.config.exceptions.SucessResponse;
import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierRequest;
import br.com.curso_udemy.product_api.module.Supplier.dto.SupplierResponse;
import br.com.curso_udemy.product_api.module.Supplier.model.Supplier;
import br.com.curso_udemy.product_api.module.category.dto.CategoryRequest;
import br.com.curso_udemy.product_api.module.category.dto.CategoryResponse;
import br.com.curso_udemy.product_api.module.category.model.Category;
import br.com.curso_udemy.product_api.module.category.repository.CategoryRepository;
import br.com.curso_udemy.product_api.module.product.repository.ProductRepository;
import br.com.curso_udemy.product_api.module.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class CategoryService {

    private static final  String  NO_CATEGORY_ID = "There are no category for the given ID.";
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CategoryResponse> findAll(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::of)
                //representação explicita .map(category -> CategoryResponse.of(category))
                .collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {

        return CategoryResponse.of(findById(id));
    }

    public Category findById(Integer id){
        validateInformedId(id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ValidationException(NO_CATEGORY_ID));
    }

    public List<CategoryResponse> findByDescription(String description){
        if(isEmpty(description)){
            throw new ValidationException("the category description must be informed");
        }
        return categoryRepository.findByDescriptionIgnoreCaseContaining(description)
                .stream()
                .map(CategoryResponse::of)
                //representação explicita .map(category -> CategoryResponse.of(category))
                .collect(Collectors.toList());
    }

    public CategoryResponse save(CategoryRequest request){
        validateCategoryNameInformed(request);
        var category =  categoryRepository.save(Category.of(request));
        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request, Integer id){
        validateCategoryNameInformed(request);
        validateInformedId(id);
        if(!categoryRepository.existsById(id)){
            throw new ValidationException(NO_CATEGORY_ID);
        }
        var category = Category.of(request);
        category.setId(id);
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }
    private void validateCategoryNameInformed(CategoryRequest request){
        if(isEmpty(request.getDescription())){
            throw new ValidationException("the category description was not informed");
        }
    }

    public SucessResponse delete(Integer id){
        validateInformedId(id);
        if(productRepository.existsByCategoryId(id)){
            throw new ValidationException("You cannot delete this category because it's already defined by a product.");
        }
        categoryRepository.deleteById(id);
        return SucessResponse.create("the category was deleted.");

    }
    private void validateInformedId(Integer id){
        if(isEmpty(id)){
            throw new ValidationException("the category id was not informed.");
        }
    }



}
