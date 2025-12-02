package org.l5g7.mealcraft.unittest.shoppingitem;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        ShoppingItemServiceImplTest.class,
        ShoppingItemControllerTest.class
})
public class ShoppingItemTestsSuite {
}

