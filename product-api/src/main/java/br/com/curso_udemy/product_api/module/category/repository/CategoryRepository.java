package br.com.curso_udemy.product_api.module.category.repository;
import br.com.curso_udemy.product_api.module.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    //dessa forma o spring data gerara uma querry com base no nome do metodo
    List<Category> findByDescriptionIgnoreCaseContaining(String description);

}
