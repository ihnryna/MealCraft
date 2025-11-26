package org.l5g7.mealcraft.app.caching;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String clearCache() {
        if (cacheManager instanceof MealCraftCacheManager mc) {
            return mc.clearAllCaches()
                    ? "All caches cleared successfully."
                    : "Failed to clear caches.";
        }
        return "Unsupported CacheManager implementation.";
    }
}
