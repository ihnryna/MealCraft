package org.l5g7.mealcraft.app.mealplan;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.recipeingredient.RecipeIngredient;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.RecipeRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItem;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemRepository;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemService;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.MealPlanColor;
import org.l5g7.mealcraft.enums.MealStatus;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private static final String ENTITY_NAME = "MealPlan";
    private static final String ENTITY_RECIPE = "Recipe";
    private static final String ENTITY_USER= "User";
    private final ShoppingItemRepository shoppingItemRepository;
    private final ShoppingItemService shoppingItemService;

    public MealPlanServiceImpl(MealPlanRepository mealPlanRepository, UserRepository userRepository, RecipeRepository recipeRepository, ShoppingItemRepository shoppingItemRepository, ShoppingItemService shoppingItemService) {
        this.mealPlanRepository = mealPlanRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.shoppingItemRepository = shoppingItemRepository;
        this.shoppingItemService = shoppingItemService;
    }

    @Override
    public List<MealPlanDto> getAllMealPlans(){
        List<MealPlan> entities = mealPlanRepository.findAll();

        return entities.stream().map(entity -> MealPlanDto.builder()
                .id(entity.getId())
                .userOwnerId(entity.getUserOwner().getId())
                .recipeId(entity.getRecipe().getId())
                .planDate(entity.getPlanDate())
                .servings(entity.getServings())
                .status(entity.getStatus())
                .name(entity.getRecipe().getName())
                .color(entity.getColor().getHex())
                .build()).toList();
    }

    @Override
    public MealPlanDto getMealPlanById(Long id){
        Optional<MealPlan> entity = mealPlanRepository.findById(id);

        if (entity.isPresent()) {
            MealPlan mealPlan = entity.get();
            return new MealPlanDto(
                    mealPlan.getId(),
                    mealPlan.getUserOwner().getId(),
                    mealPlan.getRecipe().getId(),
                    mealPlan.getPlanDate(),
                    mealPlan.getServings(),
                    mealPlan.getStatus(),
                    mealPlan.getRecipe().getName(),
                    mealPlan.getColor().getHex()
            );
        } else {
            throw new EntityDoesNotExistException("Product", String.valueOf(id));
        }

    }

    @Override
    public List<MealPlanDto> getUserMealPlans(Long userId){
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_USER, String.valueOf(userId));
        }
        List<MealPlan> entities = mealPlanRepository.findAllByUserOwner(user.get());

        return entities.stream().map(entity -> MealPlanDto.builder()
                .id(entity.getId())
                .userOwnerId(entity.getUserOwner().getId())
                .recipeId(entity.getRecipe().getId())
                .planDate(entity.getPlanDate())
                .servings(entity.getServings())
                .status(entity.getStatus())
                .name(entity.getRecipe().getName())
                .color(entity.getColor().getHex())
                .build()).toList();
    }

    @Override
    public List<MealPlanDto> getUserMealPlansBetweenDates(Long userId, Date from, Date to){
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_USER, String.valueOf(userId));
        }
        List<MealPlan> entities = mealPlanRepository.findAllByUserOwnerAndPlanDateBetween(user.get(),from,to);

        return entities.stream().map(entity -> MealPlanDto.builder()
                .id(entity.getId())
                .userOwnerId(entity.getUserOwner().getId())
                .recipeId(entity.getRecipe().getId())
                .planDate(entity.getPlanDate())
                .servings(entity.getServings())
                .status(entity.getStatus())
                .name(entity.getRecipe().getName())
                .color(entity.getColor().getHex())
                .build()).toList();
    }

    @Override
    public List<MealPlanDto> getUserMealPlansBetweenDatesWithStatus(Long userId, Date from, Date to, MealStatus status){
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_USER, String.valueOf(userId));
        }
        List<MealPlan> entities = mealPlanRepository.findMealPlanByUserOwnerAndStatusAndPlanDateBetween(user.get(),status,from,to);

        return entities.stream().map(entity -> MealPlanDto.builder()
                .id(entity.getId())
                .userOwnerId(entity.getUserOwner().getId())
                .recipeId(entity.getRecipe().getId())
                .planDate(entity.getPlanDate())
                .servings(entity.getServings())
                .status(entity.getStatus())
                .name(entity.getRecipe().getName())
                .color(entity.getColor().getHex())
                .build()).toList();
    }

    @Override
    public List<MealPlanDto> getUserMealPlansBetweenDatesWithNotStatus(Long userId, Date from, Date to, MealStatus status){
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_USER, String.valueOf(userId));
        }
        List<MealPlan> entities = mealPlanRepository.findMealPlanByUserOwnerAndStatusNotAndPlanDateBetween(user.get(),status,from,to);

        return entities.stream().map(entity -> MealPlanDto.builder()
                .id(entity.getId())
                .userOwnerId(entity.getUserOwner().getId())
                .recipeId(entity.getRecipe().getId())
                .planDate(entity.getPlanDate())
                .servings(entity.getServings())
                .status(entity.getStatus())
                .name(entity.getRecipe().getName())
                .color(entity.getColor().getHex())
                .build()).toList();
    }

    @Override
    public void createMealPlan(@Valid MealPlanDto mealPlanDto){

        User userOwner = userRepository.findById(mealPlanDto.getUserOwnerId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_USER, String.valueOf(mealPlanDto.getUserOwnerId())));


        Recipe recipe = recipeRepository.findById(mealPlanDto.getRecipeId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_RECIPE, String.valueOf(mealPlanDto.getRecipeId())));

        MealPlan entity = MealPlan.builder()
                .userOwner(userOwner)
                .recipe(recipe)
                .planDate(mealPlanDto.getPlanDate())
                .servings(mealPlanDto.getServings())
                .status(mealPlanDto.getStatus())
                .color(MealPlanColor.fromHex(mealPlanDto.getColor()))
                .build();

        mealPlanRepository.save(entity);

        for(RecipeIngredient ingredient : recipe.getIngredients()){
            shoppingItemService.addShoppingItem(new ShoppingItemDto(null, ingredient.getProduct().getName(), userOwner.getId(), ingredient.getProduct().getId(),ingredient.getAmount()*entity.getServings(),false,null,null));
        }
    }

    @Override
    public void updateMealPlan(Long id, MealPlanDto mealPlanDto){
        Optional<MealPlan> existing = mealPlanRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }

        User userOwner = userRepository.findById(mealPlanDto.getUserOwnerId())
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_USER, String.valueOf(mealPlanDto.getUserOwnerId())));

        Recipe recipe = recipeRepository.findById(mealPlanDto.getRecipeId())
                .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_RECIPE, String.valueOf(mealPlanDto.getRecipeId())));

        for(RecipeIngredient ingredient : recipe.getIngredients()){
            if(!Objects.equals(mealPlanDto.getServings(), existing.get().getServings())){
                double newAmount = ingredient.getAmount()*(mealPlanDto.getServings()-existing.get().getServings());
                shoppingItemService.addShoppingItem(new ShoppingItemDto(null, ingredient.getProduct().getName(), userOwner.getId(), ingredient.getProduct().getId(),newAmount,false,null,null));
            }
        }

        existing.get().setUserOwner(userOwner);
        existing.get().setRecipe(recipe);
        existing.get().setPlanDate(mealPlanDto.getPlanDate());
        existing.get().setServings(mealPlanDto.getServings());
        existing.get().setStatus(mealPlanDto.getStatus());
        existing.get().setColor(MealPlanColor.fromHex(mealPlanDto.getColor()));

        mealPlanRepository.save(existing.get());
    }

    @Override
    public void patchMealPlan(Long id, MealPlanDto mealPlanDto){
        Optional<MealPlan> existing = mealPlanRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }

        if(mealPlanDto.getUserOwnerId()!=null){
            User userOwner = userRepository.findById(mealPlanDto.getUserOwnerId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_USER, String.valueOf(mealPlanDto.getUserOwnerId())));
            existing.get().setUserOwner(userOwner);
        }

        if(mealPlanDto.getRecipeId()!=null){
            Recipe recipe = recipeRepository.findById(mealPlanDto.getRecipeId())
                    .orElseThrow(() -> new EntityDoesNotExistException(ENTITY_RECIPE, String.valueOf(mealPlanDto.getRecipeId())));

            existing.get().setRecipe(recipe);
            for(RecipeIngredient ingredient : recipe.getIngredients()){
                shoppingItemRepository.save(new ShoppingItem(null,existing.get().getUserOwner(),ingredient.getProduct(),ingredient.getAmount(),false,null));
            }
        }

        if(mealPlanDto.getPlanDate()!=null){
            existing.get().setPlanDate(mealPlanDto.getPlanDate());
        }

        if(mealPlanDto.getServings()!=null){
            existing.get().setServings(mealPlanDto.getServings());
        }

        if(mealPlanDto.getStatus()!=null){
            existing.get().setStatus(mealPlanDto.getStatus());
        }

        if(mealPlanDto.getColor()!=null){
            existing.get().setColor(MealPlanColor.fromHex(mealPlanDto.getColor()));
        }

        mealPlanRepository.save(existing.get());
    }

    @Override
    public void deleteMealPlan(Long id){
        Optional<MealPlan> existing = mealPlanRepository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityDoesNotExistException(ENTITY_NAME, String.valueOf(id));
        }
        for(RecipeIngredient ingredient : existing.get().getRecipe().getIngredients()){
            shoppingItemService.removeShoppingItem(new ShoppingItemDto(null, ingredient.getProduct().getName(), existing.get().getUserOwner().getId(), ingredient.getProduct().getId(), existing.get().getServings()*ingredient.getAmount(),false,null,null));
        }

        mealPlanRepository.deleteById(id);
    }
}
