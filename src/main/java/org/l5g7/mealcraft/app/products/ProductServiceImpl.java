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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "products")
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
        User currentUser = getCurrentUserOrNullIfAdmin();

        List<Product> entities = productRepository.findAll().stream()
                .filter(p -> {
                    User owner = p.getOwnerUser();
                    if (owner == null) {
                        return true;
                    }
                    return currentUser != null && owner.getId().equals(currentUser.getId());
                })
                .toList();

        return entities.stream().map(entity -> ProductDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .defaultUnitId(entity.getDefaultUnit().getId())
                .ownerUserId(entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null)
                .imageUrl(entity.getImageUrl())
                .createdAt(entity.getCreatedAt())
                .defaultUnitName(entity.getDefaultUnit().getName())
                .build()).toList();
    }

    @Override
    public ProductDto getProductById(Long id) {

        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product entity = product.get();

            User owner = entity.getOwnerUser();
            if (owner != null) {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException("Product", String.valueOf(id));
                }
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
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void createProduct(ProductDto productDto) {

        Unit unit = unitRepository.findById(productDto.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException(
                        "Unit",
                        String.valueOf(productDto.getDefaultUnitId())
                ));

        User currentUser = getCurrentUserOrNullIfAdmin();

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

        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }

        Unit unit = unitRepository.findById(productDto.getDefaultUnitId())
                .orElseThrow(() -> new EntityDoesNotExistException(
                        "Unit",
                        String.valueOf(productDto.getDefaultUnitId())
                ));

        existing.ifPresent(product -> {
            User owner = product.getOwnerUser();

            if (owner == null) {
                if (currentUser != null) {
                    throw new EntityDoesNotExistException("Product", String.valueOf(id));
                }
            } else {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException("Product", String.valueOf(id));
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
        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }

        existing.ifPresent(product -> {
            User owner = product.getOwnerUser();

            if (owner == null) {
                if (currentUser != null) {
                    throw new EntityDoesNotExistException("Product", String.valueOf(id));
                }
            } else {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException("Product", String.valueOf(id));
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
                                String.valueOf(patch.getDefaultUnitId())
                        ));
                product.setDefaultUnit(unit);
            }

            productRepository.save(product);
        });
    }

    @Override
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductById(Long id) {
        User currentUser = getCurrentUserOrNullIfAdmin();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Product", String.valueOf(id)));
        if (product.getOwnerUser() == null) {
            if (currentUser != null) {
                throw new EntityDoesNotExistException("Product", String.valueOf(id));
            }
        } else {
            if (currentUser == null || !product.getOwnerUser().getId().equals(currentUser.getId())) {
                throw new EntityDoesNotExistException("Product", String.valueOf(id));
            }
        }
        List<Recipe> recipesUsing = recipeRepository.findAllByIngredientsProduct(product);
        if (!recipesUsing.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete product because it is used in some recipes");
        }
        productRepository.delete(product);
    }


    @Override
    public List<ProductDto> searchProductsByPrefix(String prefix) {

        User currentUser = getCurrentUserOrNullIfAdmin();
        List<Product> products = productRepository.findByNameStartingWithIgnoreCase(prefix);

        return products.stream()
                .filter(p -> {
                    User owner = p.getOwnerUser();
                    if (owner == null) {
                        return true;
                    }
                    return currentUser != null && owner.getId().equals(currentUser.getId());
                })
                .map(entity -> ProductDto.builder()
                        .id(entity.getId())
                        .name(entity.getName())
                        .defaultUnitId(entity.getDefaultUnit().getId())
                        .ownerUserId(entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null)
                        .imageUrl(entity.getImageUrl())
                        .createdAt(entity.getCreatedAt())
                        .defaultUnitName(entity.getDefaultUnit().getName())
                        .build()
                ).toList();
    }

    private User getCurrentUserOrNullIfAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityDoesNotExistException("User", email));
    }
}