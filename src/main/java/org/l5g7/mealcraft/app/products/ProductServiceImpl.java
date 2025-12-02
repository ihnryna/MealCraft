package org.l5g7.mealcraft.app.products;

import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.units.Unit;
import org.l5g7.mealcraft.app.units.UnitRepository;
import org.l5g7.mealcraft.app.user.CurrentUserProvider;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "products")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final RecipeRepository recipeRepository;
    private final CurrentUserProvider currentUserProvider;
    private static final String ENTITY_NAME = "Product";

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UnitRepository unitRepository, RecipeRepository recipeRepository, CurrentUserProvider currentUserProvider) {
        this.productRepository = productRepository;
        this.unitRepository = unitRepository;
        this.recipeRepository = recipeRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Cacheable(key = "'allProducts'")
    public List<ProductDto> getAllProducts() {
        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        List<Product> entities;

        if (currentUser == null) {
            entities = productRepository.findAllByOwnerUserIsNull();
        } else {
            entities = productRepository.findAllByOwnerUserIsNullOrOwnerUser_Id(currentUser.getId());
        }

        return entities.stream()
                .map(entity -> ProductDto.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .defaultUnitId(entity.getDefaultUnit().getId())
                        .ownerUserId(entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null)
                        .imageUrl(entity.getImageUrl())
                        .createdAt(entity.getCreatedAt())
                        .defaultUnitName(entity.getDefaultUnit().getName())
                        .build())
                .toList();
    }

    @Override
    @Cacheable(key = "#id")
    public ProductDto getProductById(Long id) {

        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product entity = product.get();

            User owner = entity.getOwnerUser();
            if (owner != null && (currentUser == null || !owner.getId().equals(currentUser.getId()))) {
                throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
            }

            return new ProductDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getDefaultUnit().getId(),
                    entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null,
                    entity.getImageUrl(),
                    entity.getCreatedAt(),
                    entity.getDefaultUnit().getName()
            );
        } else {
            throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void createProduct(ProductDto productDto) {

        Unit unit = unitRepository.findById(productDto.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException(
                        "Unit",
                        "id",
                        String.valueOf(productDto.getDefaultUnitId())
                ));

        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        Product entity = Product.builder()
                .name(productDto.getName())
                .imageUrl(productDto.getImageUrl())
                .defaultUnit(unit)
                .createdAt(new Date())
                .build();

        if (currentUser != null) {
            entity.setOwnerUser(currentUser);
        }

        productRepository.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void updateProduct(Long id, ProductDto productDto) {

        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
        }

        Unit unit = unitRepository.findById(productDto.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException(
                        "Unit",
                        "id",
                        String.valueOf(productDto.getDefaultUnitId())
                ));

        existing.ifPresent(product -> {
            User owner = product.getOwnerUser();

            if (owner == null) {
                if (currentUser != null) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
                }
            } else {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
                }
            }

            product.setName(productDto.getName());
            product.setImageUrl(productDto.getImageUrl());
            product.setDefaultUnit(unit);

            productRepository.save(product);
        });
    }

    @Override
    @CacheEvict(allEntries = true)
    public void patchProduct(Long id, ProductDto patch) {
        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException(
                        ENTITY_NAME,
                        "id",
                        String.valueOf(id)
                ));

        User owner = product.getOwnerUser();

        if (owner == null) {
            if (currentUser != null) {
                throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
            }
        } else {
            if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
            }
        }

        if (patch.getName() != null) {
            product.setName(patch.getName());
        }
        if (patch.getImageUrl() != null) {
            product.setImageUrl(patch.getImageUrl());
        }
        if (patch.getDefaultUnitId() != null) {
            Unit unit = unitRepository.findById(patch.getDefaultUnitId())
                    .orElseThrow(() -> new EntityDoesNotExistException(
                            "Unit",
                            "id",
                            String.valueOf(patch.getDefaultUnitId())
                    ));
            product.setDefaultUnit(unit);
        }
        productRepository.save(product);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteProductById(Long id) {
        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id)));
        if (product.getOwnerUser() == null) {
            if (currentUser != null) {
                throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
            }
        } else {
            if (currentUser == null || !product.getOwnerUser().getId().equals(currentUser.getId())) {
                throw new EntityDoesNotExistException(ENTITY_NAME, "id", String.valueOf(id));
            }
        }
        List<Recipe> recipesUsing = recipeRepository.findAllByIngredientsProduct(product);
        if (!recipesUsing.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete product because it is used in some recipes");
        }
        productRepository.delete(product);
    }


    @Override
    @Cacheable(key = "'searchPrefix_' + #prefix")
    public List<ProductDto> searchProductsByPrefix(String prefix) {
        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();

        List<Product> products;

        if (currentUser == null) {
            products = productRepository.findAllByOwnerUserIsNullAndNameStartingWithIgnoreCase(prefix);
        } else {
            products = productRepository.findAll()
                    .stream().filter(p -> p.getOwnerUser() == null || p.getOwnerUser().getId().equals(currentUser.getId()))
                    .filter(p -> p.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                    .toList();
        }

        return products.stream()
                .map(entity -> ProductDto.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .defaultUnitId(entity.getDefaultUnit().getId())
                        .ownerUserId(entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null)
                        .imageUrl(entity.getImageUrl())
                        .createdAt(entity.getCreatedAt())
                        .defaultUnitName(entity.getDefaultUnit().getName())
                        .build())
                .toList();
    }

    @Override
    @CacheEvict(allEntries = true)
    public void addProductToRecipe(Long recipeId, Long productId, Double amount) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityDoesNotExistException(
                        "Recipe",
                        "id",
                        String.valueOf(recipeId)
                ));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityDoesNotExistException(
                        ENTITY_NAME,
                        "id",
                        String.valueOf(productId)
                ));

        RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                .product(product)
                .recipe(recipe)
                .amount(amount)
                .build();

        recipe.getIngredients().add(recipeIngredient);
    }

    @Override
    public Product getOrCreatePublicProduct(String name, Unit unit) {
        User currentUser = currentUserProvider.getCurrentUserOrNullIfAdmin();
        if (currentUser != null) {
            throw new EntityDoesNotExistException(ENTITY_NAME, "id", "create-public-product-not-allowed");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Unit must not be null");
        }

        String trimmed = name.trim();

        List<Product> publicProducts = productRepository.findAllByOwnerUserIsNull();
        for (Product p : publicProducts) {
            if (p.getName().equalsIgnoreCase(trimmed)) {
                return p;
            }
        }

        Product entity = Product.builder()
                .name(trimmed)
                .imageUrl(null)
                .defaultUnit(unit)
                .createdAt(new Date())
                .build();

        return productRepository.save(entity);
    }

}