package org.l5g7.mealcraft.unittest.notification;

import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.notification.MealExpirationNotificationService;
import org.l5g7.mealcraft.app.notification.MealExpirationScheduler;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealExpirationSchedulerTest {

    @Mock
    private MealExpirationNotificationService expirationService;

    @InjectMocks
    private MealExpirationScheduler scheduler;

    @Test
    void notifyTodayExpiringMeals_invokesServiceWithCurrentDate() {
        scheduler.notifyTodayExpiringMeals();
        verify(expirationService, times(1)).notifyForDay(any());
    }
}

