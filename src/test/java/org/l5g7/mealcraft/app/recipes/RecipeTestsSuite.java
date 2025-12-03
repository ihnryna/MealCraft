package org.l5g7.mealcraft.app.recipes;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        RecipeServiceImplTest.class,
        RecipeControllerTest.class,
        ExternalRecipeParserTest.class
})
public class RecipeTestsSuite {
}

