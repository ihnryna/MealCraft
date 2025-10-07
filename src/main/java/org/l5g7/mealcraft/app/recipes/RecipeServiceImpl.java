package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<RecipeDto> getAllRecipes() {
        List<Recipe> entities = recipeRepository.findAll();

        return entities.stream().map(entity -> RecipeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                //.ownerUserId(entity.getOwnerUser().getId())
                .baseRecipeId(entity.getBaseRecipe().getId())
                .createdAt(entity.getCreatedAt())
                .imageUrl(entity.getImageUrl())
                .build()).toList();
    }

    @Override
    public RecipeDto getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            Recipe entity = recipe.get();
            return new RecipeDto(
                    entity.getId(),
                    entity.getName(),
                    //entity.getOwnerUser().getId(),
                    entity.getBaseRecipe().getId(),
                    entity.getCreatedAt(),
                    entity.getImageUrl()
            );
        } else {
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
    }

    @Override
    public void createRecipe(RecipeDto recipeDto) {
        Optional<Recipe> existing = recipeRepository.findById(recipeDto.getId());
        if (existing.isPresent()) {
            throw new EntityAlreadyExistsException("Recipe", String.valueOf(recipeDto.getId()));
        } else {
            //User user = userRepository.findById(recipeDto.getOwnerUserId()).orElseThrow();
            Recipe baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                    .orElseThrow(() -> new EntityDoesNotExistException("Recipe", String.valueOf(recipeDto.getBaseRecipeId())));

            Recipe entity = Recipe.builder()
                    .name(recipeDto.getName())
                    //.ownerUser(user)
                    .imageUrl(recipeDto.getImageUrl())
                    .baseRecipe(baseRecipe)
                    .createdAt(recipeDto.getCreatedAt())
                    .build();
            recipeRepository.save(entity);
        }
    }

    @Override
    public void updateRecipe(Long id, RecipeDto recipeDto) {
        Optional<Recipe> existing = recipeRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
        /*User user = userRepository.findById(recipeDto.getOwnerUserId())
                .orElseThrow(() -> new EntityDoesNotExistException("User", recipeDto.getOwnerUserId()));*/
        Recipe baseRecipe = recipeRepository.findById(recipeDto.getBaseRecipeId())
                .orElseThrow(() -> new EntityDoesNotExistException("Recipe", String.valueOf(recipeDto.getBaseRecipeId())));

        existing.ifPresent(recipe -> {
            recipe.setName(recipeDto.getName());
            //recipe.setOwnerUser(user);
            recipe.setImageUrl(recipeDto.getImageUrl());
            recipe.setBaseRecipe(baseRecipe);
            recipeRepository.save(recipe);
        });
    }

    @Override
    public void patchRecipe(Long id, RecipeDto patch) {
        Optional<Recipe> existing = recipeRepository.findById(patch.getId());
        if (existing.isEmpty()){
            throw new EntityDoesNotExistException("Recipe", String.valueOf(id));
        }
        existing.ifPresent(recipe -> {
            if(patch.getName()!=null){
                recipe.setName(patch.getName());
            }
            if(patch.getImageUrl()!=null){
                recipe.setImageUrl(patch.getImageUrl());
            }
            /*if(patch.getOwnerUser()!=null){
                recipe.setOwnerUser(userRepository.findById(patch.getOwnerUserId())
                        .orElseThrow(() -> new EntityDoesNotExistException("User", patch.getOwnerUserId())));
            }*/
            if(patch.getBaseRecipeId()!=null){
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