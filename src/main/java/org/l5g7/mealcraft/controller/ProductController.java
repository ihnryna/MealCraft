package org.l5g7.mealcraft.controller;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.entity.Product;
import org.l5g7.mealcraft.entity.Recipe;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    public ProductController() {
    }

    private final List<Product> products = new ArrayList<>();


    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        throw new EntityDoesNotExistException("Product",id);
    }

    @PutMapping
    public void createProduct(@Valid @RequestBody Product product) {
        products.add(product);
    }

    @PutMapping("/{id}")
    public void updateProduct(@PathVariable Long id, @Valid @RequestBody Product updatedProduct) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                product.setName(updatedProduct.getName());
                product.setImageUrl(updatedProduct.getImageUrl());
                product.setDefaultUnitId(updatedProduct.getDefaultUnitId());
                product.setOwnerUserId(updatedProduct.getOwnerUserId());
            }
        }
    }

    @PatchMapping("/{id}")
    public void patchRecipe(@PathVariable Long id, @RequestBody Product partialUpdate) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                if (partialUpdate.getImageUrl() != null) {
                    product.setImageUrl(partialUpdate.getImageUrl());
                }
                if (partialUpdate.getOwnerUserId() != null) {
                    product.setOwnerUserId(partialUpdate.getOwnerUserId());
                }
                return;
            }
        }
        throw new EntityDoesNotExistException("Product", id);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        products.removeIf(product -> product.getId().equals(id));
    }

}
