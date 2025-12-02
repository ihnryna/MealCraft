package org.l5g7.mealcraft.app.products;

import org.l5g7.mealcraft.app.units.Unit;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    void createProduct(ProductDto product);
    void updateProduct(Long id, ProductDto productDto);
    void patchProduct(Long id, ProductDto patch);
    void deleteProductById(Long id);
    List<ProductDto> searchProductsByPrefix(String prefix);
    void addProductToRecipe(Long recipeId, Long productId, Double amount);
    Product getOrCreatePublicProduct(String name, Unit unit);
}

