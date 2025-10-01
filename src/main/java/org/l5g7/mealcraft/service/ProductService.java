package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.dto.ProductDto;
import org.l5g7.mealcraft.dto.RecipeDto;
import org.l5g7.mealcraft.entity.Product;
import org.l5g7.mealcraft.entity.Recipe;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts();
    Product getProductById(Long id);
    void createProduct(Product product);
    void updateProduct(Long id, ProductDto productDto);
    void patchProduct(Long id, ProductDto patch);
    void deleteProductById(Long id);

}

