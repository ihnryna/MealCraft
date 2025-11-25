package org.l5g7.mealcraft.config;


import org.l5g7.mealcraft.app.caching.MealCraftCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {
    @Bean
    public CacheManager cacheManager() {
         return new MealCraftCacheManager();
    }
}
