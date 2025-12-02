package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.l5g7.mealcraft.enums.MealPlanColor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mealcraft/meals")
public class ManageMealPlanController {
    private final RestClient internalApiClient;
    private final UserService userService;
    private static final String TITLE = "title";
    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String MEAL_PLAN = "mealPlan";
    private static final String HOME_PAGE = "home";

    private static final String MEAL_PLAN_FORM_FRAGMENT = "fragments/meal-plan-form :: content";


    public ManageMealPlanController(@Qualifier("internalApiClient") RestClient internalApiClient, UserService userService) {
        this.internalApiClient = internalApiClient;
        this.userService = userService;
    }

    @GetMapping("/add")
    public String showAddMealPlanForm(Model model) {
        MealPlanDto mealPlanDto = new MealPlanDto();
        mealPlanDto.setServings(1);
        mealPlanDto.setColor(MealPlanColor.PURPLE.getHex());

        ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                .uri("/recipes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<RecipeDto> recipes = response.getBody();
        List<String> recipeNames = new ArrayList<>();
        if(recipes!=null) {
            recipeNames = recipes.stream().map(RecipeDto::getName).toList();
        }
        model.addAttribute("mealColors", MealPlanColor.values());
        model.addAttribute("recipeList", recipeNames);
        model.addAttribute(MEAL_PLAN, mealPlanDto);
        model.addAttribute(TITLE, "Plan your meal");
        model.addAttribute(FRAGMENT_TO_LOAD, MEAL_PLAN_FORM_FRAGMENT);

        return HOME_PAGE;
    }
}
