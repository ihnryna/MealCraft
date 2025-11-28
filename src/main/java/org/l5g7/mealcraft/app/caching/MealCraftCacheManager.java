package org.l5g7.mealcraft.app.caching;

import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MealCraftCacheManager implements CacheManager {

    private final int TTL_MINUTES = 5;

    private final ConcurrentHashMap<String, CacheEntry> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        clearExpiredCaches();

        CacheEntry entry = cacheMap.compute(name, (k, existing) -> {
            if (existing == null) {
                LogUtils.logInfo("Creating a new cache region named: " + k);
                Cache cache = new ConcurrentMapCache(k, new ConcurrentHashMap<>(), false);
                return new CacheEntry(cache);
            }
            return existing;
        });

        return entry.cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        clearExpiredCaches();
        return cacheMap.keySet();
    }

    private void clearExpiredCaches() {
        for (Map.Entry<String, CacheEntry> e : cacheMap.entrySet()) {
            if (e.getValue().isExpired()) {
                cacheMap.remove(e.getKey());
                LogUtils.logInfo("Removed expired cache: " + e.getKey());
            }
        }
    }

    public boolean clearAllCaches() {
        cacheMap.clear();
        LogUtils.logInfo("Cleared all caches");
        return true;
    }

    private class CacheEntry {
        final Cache cache;
        private final long timestamp;

        CacheEntry(Cache cache) {
            this.cache = cache;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            return (currentTime - timestamp) > TTL_MINUTES * 60L * 1000L;
        }
    }
}
