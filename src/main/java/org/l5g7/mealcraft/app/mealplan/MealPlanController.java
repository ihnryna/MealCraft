package org.l5g7.mealcraft.app.mealplan;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.enums.MealStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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

    @GetMapping("/user/{id}")
    public List<MealPlanDto> getUserMealPlans(@PathVariable Long id) {
        return mealPlanService.getUserMealPlans(id);
    }

    @GetMapping("/user/{id}/between")
    public List<MealPlanDto> getUserMealPlansBetweenDates(
            @PathVariable Long id,
            @RequestParam Date from,
            @RequestParam Date to
    ) {
        return mealPlanService.getUserMealPlansBetweenDates(id, from, to);
    }

    @GetMapping("/user/{id}/between/status")
    public List<MealPlanDto> getUserMealPlansBetweenDatesWithStatus(
            @PathVariable Long id,
            @RequestParam Date from,
            @RequestParam Date to,
            @RequestParam MealStatus status
    ) {
        return mealPlanService.getUserMealPlansBetweenDatesWithStatus(id, from, to, status);
    }

    @GetMapping("/user/{id}/between/not-status")
    public List<MealPlanDto> getUserMealPlansBetweenDatesWithNotStatus(
            @PathVariable Long id,
            @RequestParam Date from,
            @RequestParam Date to,
            @RequestParam MealStatus status
    ) {
        return mealPlanService.getUserMealPlansBetweenDatesWithNotStatus(id, from, to, status);
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
