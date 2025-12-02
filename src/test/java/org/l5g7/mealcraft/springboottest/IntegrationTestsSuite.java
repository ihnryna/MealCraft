package org.l5g7.mealcraft.springboottest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ProductIntegrationTest.class,
        RecipeIntegrationTest.class,
        UserIntegrationTest.class
})
public class IntegrationTestsSuite {
}
