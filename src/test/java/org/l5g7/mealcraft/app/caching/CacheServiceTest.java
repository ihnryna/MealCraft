package org.l5g7.mealcraft.app.caching;

import org.junit.jupiter.api.Test;
import org.springframework.cache.support.SimpleCacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheServiceTest {

    @Test
    void clearCache() {
        CacheService cacheService = new CacheService(new MealCraftCacheManager());
        String result = cacheService.clearCache();
        assertEquals("All caches cleared successfully.", result);
    }

    @Test
    void clearCacheWithBadCacheManager() {
        CacheService cacheService = new CacheService(new SimpleCacheManager());
        String result = cacheService.clearCache();
        assertEquals("Unsupported CacheManager implementation.", result);
    }


}