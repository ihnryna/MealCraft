package org.l5g7.mealcraft.app.products;

import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UnitRepository unitRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> entities = productRepository.findAll();

        return entities.stream().map(entity -> ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .defaultUnitId(entity.getDefaultUnit().getId())
                .ownerUserId(entity.getOwnerUser().getId())
                .imageUrl(entity.getImageUrl())
                .build()).toList();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product entity = product.get();
            return new ProductDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getDefaultUnit().getId(),
                    entity.getOwnerUser().getId(),
                    entity.getImageUrl()
            );
        } else {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }
    }

    @Override
    public void createProduct(ProductDto productDto) {
        Unit unit = unitRepository.findById(productDto.getDefaultUnitId()).orElseThrow();
        User user = userRepository.findById(productDto.getOwnerUserId()).orElseThrow();
        Product entity = Product.builder()
                .name(productDto.getName())
                .imageUrl(productDto.getImageUrl())
                .defaultUnit(unit)
                .ownerUser(user)
                .build();
        productRepository.save(entity);
    }

    @Override
    public void updateProduct(Long id, ProductDto productDto) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }
        User user = userRepository.findById(productDto.getOwnerUserId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(productDto.getOwnerUserId())));

        Unit unit = unitRepository.findById(productDto.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException("Unit", String.valueOf(productDto.getDefaultUnitId())));

        existing.ifPresent(product -> {
            product.setName(productDto.getName());
            product.setImageUrl(productDto.getImageUrl());
            product.setOwnerUser(user);
            product.setDefaultUnit(unit);
            productRepository.save(product);
        });
    }

    @Override
    public void patchProduct(Long id, ProductDto patch) {
        Optional<Product> existing = productRepository.findById(patch.getId());
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }
        existing.ifPresent(product -> {
            if (patch.getName() != null) {
                product.setName(patch.getName());
            }
            if (patch.getImageUrl() != null) {
                product.setImageUrl(patch.getImageUrl());
            }
            if(patch.getDefaultUnitId() != null){
                product.setDefaultUnit(unitRepository.findById(patch.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException("Unit", String.valueOf(patch.getDefaultUnitId()))));
            }
            if(patch.getOwnerUserId()!=null){
                product.setOwnerUser(userRepository.findById(patch.getOwnerUserId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(patch.getOwnerUserId()))));
            }
            productRepository.save(product);
        });
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public void addProductToRecipe(Long productId, Long recipeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.getIngredients().add(product);
        recipeRepository.save(recipe);
    }

}