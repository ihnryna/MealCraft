package org.l5g7.mealcraft.dao;

import org.l5g7.mealcraft.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    void create(Product product);
    boolean update(Long id, Product product);
    boolean patch(Long id, Product product);
    boolean deleteById(Long id);
}
