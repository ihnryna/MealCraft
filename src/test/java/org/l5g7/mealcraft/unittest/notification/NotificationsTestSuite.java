package org.l5g7.mealcraft.unittest.notification;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        MealExpirationNotificationServiceTest.class,
        MealExpirationSchedulerTest.class,
        NotificationServiceImplTest.class
})
public class NotificationsTestSuite {
}
