package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> entities = recipeRepository.findAll();

        return entities.stream().map(entity -> {
            List<Long> ingredientsId = (entity.getIngredients() != null)
                    ? entity.getIngredients().stream()
                    .map(Product::getId)
                    .toList()
                    : List.of();

            return RecipeDto.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .ownerUserId(entity.getOwnerUser().getId())
                    .baseRecipeId(entity.getBaseRecipe().getId())
                    .createdAt(entity.getCreatedAt())
                    .imageUrl(entity.getImageUrl())
                    .ingredientsId(ingredientsId)
                    .build();
        }).toList();
    }

    @Override
    public RecipeDto getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (recipe.isPresent()) {
            Recipe entity = recipe.get();

            List<Long> ingredientsId = entity.getIngredients() != null
                    ? entity.getIngredients().stream()
                    .map(Product::getId)
                    .toList()
                    : List.of();

            return new RecipeDto(
                    entity.getId(),
                    entity.getName(),
                    entity.getOwnerUser().getId(),
                    entity.getBaseRecipe().getId(),
                    entity.getCreatedAt(),
                    entity.getImageUrl(),
                    ingredientsId
            );
        } else {
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
    }

    @Override
    public void createRecipe(RecipeDto recipeDto) {
        User user = userRepository.findById(recipeDto.getOwnerUserId()).orElseThrow();
        Recipe baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                .orElseThrow(() -> new EntityDoesNotExistException("Recipe", String.valueOf(recipeDto.getBaseRecipeId())));

        List<Product> ingredients = productRepository.findAllById(recipeDto.getIngredientsId());

        if (ingredients.size() != recipeDto.getIngredientsId().size()) {
            throw new RuntimeException("One or more products not found");
        }

        Recipe entity = Recipe.builder()
                .name(recipeDto.getName())
                .ownerUser(user)
                .imageUrl(recipeDto.getImageUrl())
                .baseRecipe(baseRecipe)
                .createdAt(recipeDto.getCreatedAt())
                .ingredients(ingredients)
                .build();
        recipeRepository.save(entity);

    }

    @Override
    public void updateRecipe(Long id, RecipeDto recipeDto) {
        Optional<Recipe> existing = recipeRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
        User user = userRepository.findById(recipeDto.getOwnerUserId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(recipeDto.getOwnerUserId())));
        Recipe baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                .orElseThrow(() -> new EntityDoesNotExistException("Recipe", String.valueOf(recipeDto.getBaseRecipeId())));


        existing.ifPresent(recipe -> {
            List<Product> ingredients = productRepository.findAllById(recipeDto.getIngredientsId());

            recipe.setName(recipeDto.getName());
            recipe.setOwnerUser(user);
            recipe.setImageUrl(recipeDto.getImageUrl());
            recipe.setBaseRecipe(baseRecipe);
            recipe.setIngredients(ingredients);
            recipeRepository.save(recipe);
        });
    }

    @Override
    public void patchRecipe(Long id, RecipeDto patch) {
        Optional<Recipe> existing = recipeRepository.findById(patch.getId());
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
        existing.ifPresent(recipe -> {
            if (patch.getName() != null) {
                recipe.setName(patch.getName());
            }
            if (patch.getImageUrl() != null) {
                recipe.setImageUrl(patch.getImageUrl());
            }
            if(patch.getIngredientsId() != null){
                List<Long> ingredientsId = recipe.getIngredients() != null
                        ? recipe.getIngredients().stream()
                        .map(Product::getId)
                        .toList()
                        : List.of();

                List<Product> ingredients = productRepository.findAllById(patch.getIngredientsId());

                recipe.setIngredients(ingredients);
            }
            if(patch.getOwnerUserId()!=null){
                recipe.setOwnerUser(userRepository.findById(patch.getOwnerUserId())
                        .orElseThrow(() -> new EntityDoesNotExistException("User", String.valueOf(patch.getOwnerUserId()))));
            }
            if (patch.getBaseRecipeId() != null) {
                recipe.setBaseRecipe(recipeRepository.findById(patch.getBaseRecipeId())
                        .orElseThrow(() -> new EntityDoesNotExistException("Recipe", String.valueOf(patch.getBaseRecipeId()))));
            }
            recipeRepository.save(recipe);
        });
    }

    @Override
    public void deleteRecipeById(Long id) {
        recipeRepository.deleteById(id);
    }
}