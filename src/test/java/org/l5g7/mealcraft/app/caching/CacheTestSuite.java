package org.l5g7.mealcraft.app.caching;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Test suite for all caching-related tests.
 * This suite organizes all tests for the cache functionality including
 * unit tests, web layer tests, and integration tests.
 */
@Suite
@SelectClasses({
        CacheControllerTest.class,
        CacheControllerWebMvcTest.class,
        CacheControllerIntegrationTest.class,
        CacheControllerEdgeCaseTest.class
})
public class CacheTestSuite {
}
