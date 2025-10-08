package org.l5g7.mealcraft.app.products;

import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    void createProduct(ProductDto product);
    void updateProduct(Long id, ProductDto productDto);
    void patchProduct(Long id, ProductDto patch);
    void deleteProductById(Long id);
    void addProductToRecipe(Long productId, Long recipeId);

}

