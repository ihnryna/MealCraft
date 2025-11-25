package org.l5g7.mealcraft.caching;

import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MealCraftCacheManager implements CacheManager {

    private final int TTL_MINUTES = 5;

    // A map to hold our active caches (name -> CacheEntry)
    private final ConcurrentHashMap<String, CacheEntry> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        // remove expired entries first
        clearExpiredCaches();

        // create or reuse a CacheEntry; return its Cache
        CacheEntry entry = cacheMap.compute(name, (k, existing) -> {
            if (existing == null || existing.isExpired()) {
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
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    private void clearExpiredCaches() {
        for (Map.Entry<String, CacheEntry> e : cacheMap.entrySet()) {
            if (e.getValue().isExpired()) {
                cacheMap.remove(e.getKey());
                LogUtils.logInfo("Removed expired cache: " + e.getKey());
            }
        }
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
