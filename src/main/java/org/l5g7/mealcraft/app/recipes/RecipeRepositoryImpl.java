//package org.l5g7.mealcraft.app.recipes;
//
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class RecipeRepositoryImpl implements RecipeRepository {
//
//    Recipe recipe1 = new Recipe(11L, 1L, null, "Fried egg", LocalDateTime.parse("2025-09-21T16:36:07"), null);
//    Recipe recipe2 = new Recipe(21L, 1L, 11L, "My SUPER fried egg", LocalDateTime.parse("2025-09-21T20:36:07"), null);
//    Recipe recipe3 = new Recipe(31L, 2L, null, "Borsch", LocalDateTime.parse("2025-09-21T16:40:07"), null);
//    List<Recipe> recipes = new ArrayList<Recipe>(List.of(recipe1,recipe2,recipe3));
//
//
//    @Override
//    public List<Recipe> findAll() {
//        return recipes;
//    }
//
//    @Override
//    public Optional<Recipe> findById(Long id) {
//        return recipes
//                .stream()
//                .filter(user -> user.getId().equals(id))
//                .findFirst();
//    }
//
//    @Override
//    public void create(Recipe recipe) {
//        recipes.add(recipe);
//    }
//    @Override
//    public boolean patch(Long id, Recipe recipe) {
//        for (Recipe r : recipes) {
//            if (r.getId().equals(recipe.getId())) {
//                if(recipe.getBaseRecipeId()!=null){
//                    r.setBaseRecipeId(recipe.getBaseRecipeId());
//                }
//                if(recipe.getName()!=null){
//                    r.setName(recipe.getName());
//                }
//                if(recipe.getOwnerUserId()!=null){
//                    r.setOwnerUserId(recipe.getOwnerUserId());
//                }
//                if(recipe.getImageUrl()!=null){
//                    r.setImageUrl(recipe.getImageUrl());
//                }
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean update(Long id, Recipe recipe) {
//        for (Recipe r : recipes) {
//            if (r.getId().equals(recipe.getId())) {
//                r.setBaseRecipeId(recipe.getBaseRecipeId());
//                r.setOwnerUserId(recipe.getOwnerUserId());
//                r.setName(recipe.getName());
//                r.setImageUrl(recipe.getImageUrl());
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public boolean deleteById(Long id) {
//        return recipes.removeIf(recipe -> recipe.getId().equals(id));
//    }
//}*/