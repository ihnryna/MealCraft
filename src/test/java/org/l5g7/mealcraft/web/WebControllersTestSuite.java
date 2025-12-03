package org.l5g7.mealcraft.web;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        LandingPageWebControllerTest.class,
        AdminHomeWebControllerTest.class,
        AdminUserWebControllerTest.class,
        AdminUnitWebControllerTest.class,
        AdminProductWebControllerTest.class,
        AdminRecipeWebControllerTest.class,
        AdminStatisticsWebControllerTest.class,
        AdminUnitWebControllerTest.class,
        AdminUserWebControllerTest.class,
        AuthWebControllerTest.class,
        HomeWebControllerTest.class,
        ManageMealPlanControllerTest.class
})
public class WebControllersTestSuite {
}

