package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.dao.RecipeRepository;
import org.l5g7.mealcraft.dto.RecipeDto;
import org.l5g7.mealcraft.entity.Recipe;
import org.l5g7.mealcraft.entity.User;
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
    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            return recipe.get();
        } else {
            throw new EntityDoesNotExistException("Recipe", id);
        }
    }

    @Override
    public void createRecipe(Recipe recipe) {
        Optional<Recipe> existing = recipeRepository.findById(recipe.getId());
        if (existing.isPresent()) {
            throw new EntityAlreadyExistsException("Recipe", recipe.getId());
        } else {
            recipeRepository.create(recipe);
        }
    }

    @Override
    public void updateRecipe(Long id, RecipeDto recipeDto) {
        Optional<Recipe> existing = recipeRepository.findById(recipeDto.getId());
        if (existing.isEmpty()){
            throw new EntityDoesNotExistException("Recipe", id);
        }
        recipeRepository.update(id, new Recipe(recipeDto));
    }

    @Override
    public void patchRecipe(Long id, RecipeDto patch) {
        updateRecipe(id, patch);
    }

    @Override
    public void deleteRecipeById(Long id) {
        boolean deleted = recipeRepository.deleteById(id);
        if (!deleted) {
            throw new EntityDoesNotExistException("User", id);
        }
    }
}