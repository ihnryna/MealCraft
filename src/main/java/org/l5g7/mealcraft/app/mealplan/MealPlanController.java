package org.l5g7.mealcraft.app.mealplan;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meal-plans")
public class MealPlanController {
    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public List<MealPlanDto> getAll() {
        return mealPlanService.getAllMealPlans();
    }

    @GetMapping("/{id}")
    public MealPlanDto getMealPlan(@PathVariable Long id) {
        return mealPlanService.getMealPlanById(id);
    }

    @PostMapping
    public void createMealPlan(@Valid @RequestBody MealPlanDto mealPlan) {
        mealPlanService.createMealPlan(mealPlan);
    }

    @PutMapping("/{id}")
    public void updateMealPlan(@PathVariable Long id, @Valid @RequestBody MealPlanDto updatedMealPlan) {
        mealPlanService.updateMealPlan(id, updatedMealPlan);
    }

    @PatchMapping("/{id}")
    public void patchMealPlan(@PathVariable Long id, @RequestBody MealPlanDto partialUpdate) {
        mealPlanService.patchMealPlan(id, partialUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteMealPlan(@PathVariable Long id) {
        mealPlanService.deleteMealPlan(id);
    }
}
