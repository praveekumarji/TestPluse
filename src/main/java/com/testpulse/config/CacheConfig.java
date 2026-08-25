package com.testpulse.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache Configuration for TestPulse Application
 * 
 * This configuration enables caching across the application to reduce unnecessary database calls.
 * Uses ConcurrentMapCacheManager for in-memory caching with the following cache names:
 * - users: Caches user data by email, mobile number, and ID
 * - tests: Caches test lists and individual test details
 * - questions: Caches questions grouped by test ID and language
 * - subscriptionPlans: Caches subscription plan information
 * - coupons: Caches coupon data and validation results
 * - bookmarks: Caches user bookmarks
 * - appConfig: Caches application configuration values
 * 
 * For production environments with higher traffic, consider using:
 * - Caffeine Cache: Better performance with TTL and size limits
 * - Redis: Distributed caching across multiple instances
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure ConcurrentMapCacheManager for in-memory caching
     * 
     * Cache Names:
     * - users: User lookups (email, mobile, ID)
     * - tests: Test queries and details
     * - questions: Questions by test ID
     * - subscriptionPlans: Subscription plan list
     * - coupons: Coupon information
     * - bookmarks: User bookmarks
     * - appConfig: Configuration values
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "users",
                "tests",
                "questions",
                "subscriptionPlans",
                "coupons",
                "bookmarks",
                "appConfig"
        );
    }
}
