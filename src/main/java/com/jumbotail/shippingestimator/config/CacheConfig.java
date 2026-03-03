package com.jumbotail.shippingestimator.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the application.
 *
 * <p>Enables Spring Cache abstraction and configures a simple in-memory
 * cache manager using ConcurrentMapCacheManager.</p>
 *
 * <p>Caches:
 * <ul>
 *   <li><b>nearestWarehouse</b> – Caches nearest warehouse results for seller-product pairs</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("nearestWarehouse");
    }
}
