package org.l5g7.mealcraft.app.aop.loginaspect;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all LogInLimitAspect related tests.
 * This suite organizes all tests for the login limiting aspect functionality.
 */
@Suite
@SelectClasses({
        LogInLimitAspectTest.class,
        LogInLimitAspectIntegrationTest.class,
        WriteLogLimitExceededResponseEdgeCaseTest.class
})
public class LogInLimitAspectTestSuite {
}
