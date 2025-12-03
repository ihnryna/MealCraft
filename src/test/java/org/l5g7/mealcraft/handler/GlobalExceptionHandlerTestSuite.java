package org.l5g7.mealcraft.handler;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all GlobalExceptionHandler related tests.
 * This suite organizes all tests for the global exception handling functionality.
 */
@Suite
@SelectClasses({
        GlobalExceptionHandlerTest.class,
        HandleEntityAlreadyExistsTest.class
})
public class GlobalExceptionHandlerTestSuite {
}
