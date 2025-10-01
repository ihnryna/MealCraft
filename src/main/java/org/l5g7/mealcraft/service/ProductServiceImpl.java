package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.dao.ProductRepository;
import org.l5g7.mealcraft.dao.RecipeRepository;
import org.l5g7.mealcraft.dto.ProductDto;
import org.l5g7.mealcraft.dto.RecipeDto;
import org.l5g7.mealcraft.entity.Product;
import org.l5g7.mealcraft.entity.Recipe;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new EntityDoesNotExistException("Product", id);
        }
    }

    @Override
    public void createProduct(Product product) {
        Optional<Product> existing = productRepository.findById(product.getId());
        if (existing.isPresent()) {
            throw new EntityAlreadyExistsException("Product", product.getId());
        } else {
            productRepository.create(product);
        }
    }

    @Override
    public void updateProduct(Long id, ProductDto productDto) {
        Optional<Product> existing = productRepository.findById(productDto.getId());
        if (existing.isEmpty()){
            throw new EntityDoesNotExistException("Product", id);
        }
        productRepository.update(id, new Product(productDto));
    }

    @Override
    public void patchProduct(Long id, ProductDto patch) {
        Optional<Product> existing = productRepository.findById(patch.getId());
        if (existing.isEmpty()){
            throw new EntityDoesNotExistException("Product", id);
        }
        productRepository.update(id, new Product(patch));
    }

    @Override
    public void deleteProductById(Long id) {
        boolean deleted = productRepository.deleteById(id);
        if (!deleted) {
            throw new EntityDoesNotExistException("Product", id);
        }
    }
}