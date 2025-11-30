package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        MealPlanServiceImplTest.class,
        EventCellTest.class
})
public class MealCraftUnitTestsSuite {
}
