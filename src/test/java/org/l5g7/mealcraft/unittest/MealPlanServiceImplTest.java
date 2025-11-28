package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.mealplan.MealPlan;
import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.mealplan.MealPlanRepository;
import org.l5g7.mealcraft.app.mealplan.MealPlanServiceImpl;
import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.enums.MealStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealPlanServiceImplTest {

    @Mock
    private MealPlanRepository mealPlanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MealPlanServiceImpl mealPlanService;

    private User testUser;
    private Recipe testRecipe;
    private MealPlan mealPlan1;
    private MealPlan mealPlan2;

    @BeforeEach
    void setUp() {

        LocalDate localPlanDate1 = LocalDate.of(2025, 11, 3); // 3 Nov 2025
        LocalDate localPlanDate2 = LocalDate.of(2025, 11, 5);   // 5 Nov 2025

        Date planDate1 = Date.from(localPlanDate1.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date planDate2 = Date.from(localPlanDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());

        testUser = User.builder()
                .id(1L)
                .build();

        testRecipe = Recipe.builder()
                .id(100L)
                .name("Test Recipe")
                .createdAt(new Date())
                .build();

        mealPlan1 = MealPlan.builder()
                .id(10L)
                .userOwner(testUser)
                .recipe(testRecipe)
                .planDate(planDate1) // 3 Nov 2025
                .servings(2)
                .status(MealStatus.PLANNED)
                .build();

        mealPlan2 = MealPlan.builder()
                .id(11L)
                .userOwner(testUser)
                .recipe(testRecipe)
                .planDate(planDate2) // 5 Nov 2025
                .servings(3)
                .status(MealStatus.PLANNED)
                .build();
    }

    @Test
    void testGetUserMealPlans_UserExists_ReturnsMealPlans() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mealPlanRepository.findAllByUserOwner(testUser))
                .thenReturn(List.of(mealPlan1, mealPlan2));

        List<MealPlanDto> result = mealPlanService.getUserMealPlans(1L);

        assertEquals(2, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(11L, result.get(1).getId());
        verify(userRepository).findById(1L);
        verify(mealPlanRepository).findAllByUserOwner(testUser);
    }

    @Test
    void testGetUserMealPlansBetweenDates_ReturnsFilteredMealPlans() {
        LocalDate fromDate = LocalDate.of(2025, 11, 2); // 2 Nov 2025
        LocalDate toDate = LocalDate.of(2025, 11, 4);   // 4 Nov 2025

        Date from = Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mealPlanRepository.findAllByUserOwnerAndPlanDateBetween(testUser, from, to))
                .thenReturn(List.of(mealPlan1));

        List<MealPlanDto> result = mealPlanService.getUserMealPlansBetweenDates(1L, from, to);

        assertEquals(1, result.size());
        assertEquals(mealPlan1.getId(), result.get(0).getId());
        verify(userRepository).findById(1L);
        verify(mealPlanRepository).findAllByUserOwnerAndPlanDateBetween(testUser, from, to);
    }

}