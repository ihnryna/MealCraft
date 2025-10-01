package org.l5g7.mealcraft.dao;

import org.l5g7.mealcraft.entity.Product;
import org.l5g7.mealcraft.entity.Recipe;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    Product product1 = new Product(111L, "Egg", 1111L, 1L, null);
    Product product2 = new Product(211L, "Beetroot", 2222L, 2L, null);
    List<Product> products = new ArrayList<Product>(List.of(product1,product2));


    @Override
    public List<Product> findAll() {
        return products;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }

    @Override
    public void create(Product product) {
        products.add(product);
    }

    @Override
    public boolean update(Long id, Product product) {
        for (Product p : products) {
            if (p.getId().equals(product.getId())) {
                p.setDefaultUnitId(product.getDefaultUnitId());
                p.setOwnerUserId(product.getOwnerUserId());
                p.setName(product.getName());
                p.setImageUrl(product.getImageUrl());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        return products.removeIf(product -> product.getId().equals(id));
    }
}