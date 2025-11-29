package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredientDto;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipe;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.RecipeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@CacheConfig(cacheNames = "recipes")
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RecipeProvider recipeProvider;
    private static final String ENTITY_NAME = "Recipe";

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, ProductRepository productRepository, UserRepository userRepository, RecipeProvider recipeProvider) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.recipeProvider = recipeProvider;
    }

    @Override
    public List<RecipeDto> getAllRecipes() {
        User currentUser = getCurrentUserOrNullIfAdmin();

        List<Recipe> entities = recipeRepository.findAll();

        return entities.stream()
                .filter(entity -> {
                    User owner = entity.getOwnerUser();
                    if (owner == null) {
                        return true;
                    }
                    return currentUser != null && owner.getId().equals(currentUser.getId());
                })
                .map(entity -> {
                    List<RecipeIngredientDto> ingredients = (entity.getIngredients() != null)
                            ? entity.getIngredients().stream()
                            .map(ingredient -> RecipeIngredientDto.builder()
                                    .id(ingredient.getId())
                                    .productId(ingredient.getProduct().getId())
                                    .productName(ingredient.getProduct().getName())
                                    .amount(ingredient.getAmount())
                                    .build())
                            .toList()
                            : List.of();

                    return RecipeDto.builder()
                            .id(entity.getId())
                            .name(entity.getName())
                            .ownerUserId(entity.getOwnerUser() != null ? entity.getOwnerUser().getId() : null)
                            .baseRecipeId(entity.getBaseRecipe() != null ? entity.getBaseRecipe().getId() : null)
                            .createdAt(entity.getCreatedAt())
                            .imageUrl(entity.getImageUrl())
                            .ingredients(ingredients)
                            .build();
                }).toList();
    }

    @Override
    public RecipeDto getRecipeById(Long id) {
        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (recipe.isPresent()) {
            Recipe entity = recipe.get();

            User owner = entity.getOwnerUser();
            if (owner != null) {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
                }
            }

            List<RecipeIngredientDto> ingredients = entity.getIngredients() != null
                    ? entity.getIngredients().stream()
                    .map(ingredient -> RecipeIngredientDto.builder()
                            .id(ingredient.getId())
                            .productId(ingredient.getProduct().getId())
                            .productName(ingredient.getProduct().getName())
                            .amount(ingredient.getAmount())
                            .build())
                    .toList()
                    : List.of();

            return new RecipeDto(
                    entity.getId(),
                    entity.getName(),
                    owner != null ? owner.getId() : null,
                    entity.getBaseRecipe() != null ? entity.getBaseRecipe().getId() : null,
                    entity.getCreatedAt(),
                    entity.getImageUrl(),
                    ingredients
            );
        } else {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    public void createRecipe(RecipeDto recipeDto) {

        User currentUser = getCurrentUserOrNullIfAdmin();

        Recipe baseRecipe = null;
        if (recipeDto.getBaseRecipeId() != null) {
            baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(recipeDto.getBaseRecipeId())));
        }

        Recipe entity = Recipe.builder()
                .name(recipeDto.getName())
                .ownerUser(currentUser)
                .imageUrl(recipeDto.getImageUrl())
                .baseRecipe(baseRecipe)
                .createdAt(new Date())
                .build();

        List<RecipeIngredient> ingredients = new ArrayList<>();
        if (recipeDto.getIngredients() != null && !recipeDto.getIngredients().isEmpty()) {

            Set<Long> usedProducts = new HashSet<>();

            for (RecipeIngredientDto ingDto : recipeDto.getIngredients()) {

                Long productId = ingDto.getProductId();
                if (productId == null) {
                    throw new IllegalArgumentException("Product must be selected for each ingredient");
                }

                if (!usedProducts.add(productId)) {
                    throw new IllegalArgumentException("Recipe cannot contain the same product more than once");
                }

                if (ingDto.getAmount() == null || ingDto.getAmount().doubleValue() <= 0) {
                    throw new IllegalArgumentException("Ingredient amount must be positive");
                }

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new EntityDoesNotExistException("Product", String.valueOf(productId)));

                RecipeIngredient ingredient = RecipeIngredient.builder()
                        .recipe(entity)
                        .product(product)
                        .amount(ingDto.getAmount())
                        .build();

                ingredients.add(ingredient);
            }
        }

        entity.setIngredients(ingredients);

        recipeRepository.save(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    public void updateRecipe(Long id, RecipeDto recipeDto) {
        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Recipe> existing = recipeRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }

        User user;
        if (recipeDto.getOwnerUserId() != null) {
            user = userRepository.findById(recipeDto.getOwnerUserId())
                    .orElseThrow(() -> new EntityDoesNotExistException(
                            "User",
                            String.valueOf(recipeDto.getOwnerUserId())
                    ));
        } else {
            user = null;
        }

        Recipe baseRecipe;
        if (recipeDto.getBaseRecipeId() != null) {
            baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(recipeDto.getBaseRecipeId())));
        } else {
            baseRecipe = null;
        }

        existing.ifPresent(recipe -> {
            User owner = recipe.getOwnerUser();

            if (owner == null) {
                if (currentUser != null) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
                }
            } else {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
                }
            }

            recipe.setName(recipeDto.getName());
            recipe.setOwnerUser(user);
            recipe.setImageUrl(recipeDto.getImageUrl());
            recipe.setBaseRecipe(baseRecipe);

            List<RecipeIngredient> ingredients = new ArrayList<>();

            if (recipeDto.getIngredients() != null && !recipeDto.getIngredients().isEmpty()) {

                Set<Long> usedProducts = new HashSet<>();

                for (RecipeIngredientDto ingDto : recipeDto.getIngredients()) {

                    Long productId = ingDto.getProductId();
                    if (productId == null) {
                        throw new IllegalArgumentException("Product must be selected for each ingredient");
                    }

                    if (!usedProducts.add(productId)) {
                        throw new IllegalArgumentException("Recipe cannot contain the same product more than once");
                    }

                    if (ingDto.getAmount() == null || ingDto.getAmount().doubleValue() <= 0) {
                        throw new IllegalArgumentException("Ingredient amount must be positive");
                    }

                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityDoesNotExistException(
                                    "Product",
                                    String.valueOf(productId)
                            ));

                    RecipeIngredient ingredient = RecipeIngredient.builder()
                            .recipe(recipe)
                            .product(product)
                            .amount(ingDto.getAmount())
                            .build();

                    ingredients.add(ingredient);
                }
            }

            recipe.getIngredients().clear();
            recipe.getIngredients().addAll(ingredients);

            recipeRepository.save(recipe);
        });
    }

    @Override
    @CacheEvict(allEntries = true)
    public void patchRecipe(Long id, RecipeDto patch) {
        User currentUser = getCurrentUserOrNullIfAdmin();

        Optional<Recipe> existing = recipeRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }

        existing.ifPresent(recipe -> {
            User owner = recipe.getOwnerUser();

            if (owner == null) {
                if (currentUser != null) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
                }
            } else {
                if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                    throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
                }
            }

            if (patch.getName() != null) {
                recipe.setName(patch.getName());
            }

            if (patch.getImageUrl() != null) {
                recipe.setImageUrl(patch.getImageUrl());
            }

            if (patch.getIngredients() != null) {
                List<RecipeIngredient> ingredients = new ArrayList<>();
                Set<Long> usedProducts = new HashSet<>();

                for (RecipeIngredientDto ingDto : patch.getIngredients()) {
                    if (ingDto == null) {
                        continue;
                    }

                    Long productId = ingDto.getProductId();
                    if (productId == null) {
                        throw new IllegalArgumentException("Product must be selected for each ingredient");
                    }

                    if (!usedProducts.add(productId)) {
                        throw new IllegalArgumentException("Recipe cannot contain the same product more than once");
                    }

                    if (ingDto.getAmount() == null || ingDto.getAmount().doubleValue() <= 0) {
                        throw new IllegalArgumentException("Ingredient amount must be positive");
                    }

                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityDoesNotExistException(
                                    "Product",
                                    String.valueOf(productId)
                            ));

                    RecipeIngredient ingredient = RecipeIngredient.builder()
                            .recipe(recipe)
                            .product(product)
                            .amount(ingDto.getAmount())
                            .build();

                    ingredients.add(ingredient);
                }

                recipe.getIngredients().clear();
                recipe.getIngredients().addAll(ingredients);
            }

            if (patch.getOwnerUserId() != null) {
                recipe.setOwnerUser(userRepository.findById(patch.getOwnerUserId())
                        .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(patch.getOwnerUserId()))));
            }

            if (patch.getBaseRecipeId() != null) {
                recipe.setBaseRecipe(recipeRepository.findById(patch.getBaseRecipeId())
                        .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(patch.getBaseRecipeId()))));
            }

            recipeRepository.save(recipe);
        });
    }

    @Override
    @CacheEvict(allEntries = true)
    public void deleteRecipeById(Long id) {

        User currentUser = getCurrentUserOrNullIfAdmin();

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id)));

        User owner = recipe.getOwnerUser();

        if (owner == null) {
            if (currentUser != null) {
                throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
            }
        } else {
            if (currentUser == null || !owner.getId().equals(currentUser.getId())) {
                throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
            }
        }

        List<Recipe> children = recipeRepository.findAllByBaseRecipe(recipe);
        if (!children.isEmpty()) {
            for (Recipe child : children) {
                child.setBaseRecipe(null);
            }
            recipeRepository.saveAll(children);
        }
        recipeRepository.deleteById(id);
    }


    @Override
    public RecipeDto getRandomRecipe() throws NoSuchElementException {
        ExternalRecipe externalRecipe = recipeProvider.getRandomRecipe();

        return RecipeDto.builder()
                .id(externalRecipe.id())
                .name(externalRecipe.name())
                .imageUrl(externalRecipe.imageUrl())
                .build();
    }

    private User getCurrentUserOrNullIfAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityDoesNotExistException("User", email));
    }
}