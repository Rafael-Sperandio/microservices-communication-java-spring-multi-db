package br.com.curso_udemy.product_api.module.product.repository;

import br.com.curso_udemy.product_api.module.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository eh @repository
//find all e find by id
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findByNameIgnoreCaseContaining(String name);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findBySupplierId(Integer supplierId);

    boolean existsByCategoryId(Integer categoryId);

    boolean existsBySupplierId(Integer supplierId);
}
