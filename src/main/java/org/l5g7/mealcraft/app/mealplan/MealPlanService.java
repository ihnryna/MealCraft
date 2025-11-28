package org.l5g7.mealcraft.app.mealplan;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.enums.MealStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MealPlanService {
    List<MealPlanDto> getAllMealPlans();
    MealPlanDto getMealPlanById(Long id);
    List<MealPlanDto> getUserMealPlans(Long userId);
    List<MealPlanDto> getUserMealPlansBetweenDates(Long userId, Date from, Date to);
    List<MealPlanDto> getUserMealPlansBetweenDatesWithStatus(Long userId, Date from, Date to, MealStatus status);
    List<MealPlanDto> getUserMealPlansBetweenDatesWithNotStatus(Long userId, Date from, Date to, MealStatus status);
    void createMealPlan(@Valid MealPlanDto mealPlanDto);
    void updateMealPlan(Long id, MealPlanDto mealPlanDto);
    void patchMealPlan(Long id, MealPlanDto mealPlanDto);
    void deleteMealPlan(Long id);
}
