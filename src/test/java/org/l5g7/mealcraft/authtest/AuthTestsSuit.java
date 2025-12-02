package org.l5g7.mealcraft.authtest;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    AuthControllerTest.class,
    AuthServiceTest.class,
    JwtKeyTest.class,
    JwtServiceTest.class
})

public class AuthTestsSuit {
}
