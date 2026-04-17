package br.com.curso_udemy.product_api.module.Supplier.repository;

import br.com.curso_udemy.product_api.module.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.curso_udemy.product_api.module.Supplier.model.Supplier;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier,Integer> {

    List<Supplier> findByNameIgnoreCaseContaining(String name);
}
