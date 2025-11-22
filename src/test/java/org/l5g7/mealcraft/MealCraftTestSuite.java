package org.l5g7.mealcraft;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.l5g7.mealcraft.webmvctest.RecipeControllerWithServiceWebMvcTest;
import org.l5g7.mealcraft.webmvctest.UserControllerWebMvcTest;
import org.l5g7.mealcraft.unittest.ProductServiceUnitTests;
import org.l5g7.mealcraft.springboottest.UserIntegrationTest;
import org.l5g7.mealcraft.springsecuritytest.SecurityTests;

@Suite
@SelectClasses({
        UserIntegrationTest.class,
        ProductServiceUnitTests.class,
        RecipeControllerWithServiceWebMvcTest.class,
        UserControllerWebMvcTest.class,
        SecurityTests.class
})
public class MealCraftTestSuite{
}
