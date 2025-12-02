package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.l5g7.mealcraft.enums.MealPlanColor;
import org.l5g7.mealcraft.enums.MealStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Controller
@RequestMapping("/mealcraft/meals")
public class ManageMealPlanController {
    private final RestClient internalApiClient;
    private final UserService userService;
    private static final String TITLE = "title";
    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String MEAL_PLAN = "mealPlan";
    private static final String HOME_PAGE = "home";
    private static final String REDIRECT_HOME_PAGE = "redirect:/mealcraft/home";



    private static final String MEAL_PLAN_FORM_FRAGMENT = "fragments/meal-plan-form :: content";


    public ManageMealPlanController(@Qualifier("internalApiClient") RestClient internalApiClient, UserService userService) {
        this.internalApiClient = internalApiClient;
        this.userService = userService;
    }

    @GetMapping("/add/{day}")
    public String showAddMealPlanForm(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        MealPlanDto mealPlanDto = new MealPlanDto();
        mealPlanDto.setServings(1);
        mealPlanDto.setColor(MealPlanColor.PURPLE.getHex());
        Date d = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
        mealPlanDto.setPlanDate(d);
        mealPlanDto.setStatus(MealStatus.PLANNED);
        mealPlanDto.setUserOwnerId(userService.getUserByUsername(username).id());


        addRecipeListToModel(model);
        model.addAttribute("mealColors", MealPlanColor.values());
        model.addAttribute(MEAL_PLAN, mealPlanDto);
        model.addAttribute(TITLE, "Plan your meal");
        model.addAttribute(FRAGMENT_TO_LOAD, MEAL_PLAN_FORM_FRAGMENT);

        return HOME_PAGE;
    }

    @PostMapping("/save")
    public String saveMealPlan(@ModelAttribute("mealPlan") MealPlanDto mealPlanDto, Model model) {

        try {
            internalApiClient
                    .post()
                    .uri("/meal-plans")
                    .body(mealPlanDto)
                    .retrieve()
                    .toBodilessEntity();

            return REDIRECT_HOME_PAGE;

        } catch (RestClientResponseException ex) {

            String message;
            String body = ex.getResponseBodyAsString();

            if (!body.isBlank()) {
                message = body;
            } else {
                message = "Failed to save meal plan: " + ex.getStatusCode();
            }


            addRecipeListToModel(model);

            model.addAttribute(MEAL_PLAN, mealPlanDto);
            model.addAttribute("mealColors", MealPlanColor.values());
            model.addAttribute(TITLE, "Create meal plan");
            model.addAttribute(FRAGMENT_TO_LOAD, MEAL_PLAN_FORM_FRAGMENT);
            model.addAttribute("errorMessage", message);

            return HOME_PAGE;
        }
    }

    private void addRecipeListToModel(Model model) {
        //TODO: USERS+PUBLIC recipes

        ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                .uri("/recipes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<RecipeDto> recipes = response.getBody();

        assert recipes != null;
        List<Map<String, Object>> recipeList = recipes.stream()
                .map(r -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", r.getId());
                    m.put("name", r.getName());
                    return m;
                })
                .toList();

        model.addAttribute("recipeList", recipeList);
    }

}
