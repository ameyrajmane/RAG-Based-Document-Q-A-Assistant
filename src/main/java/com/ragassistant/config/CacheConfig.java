package com.ragassistant.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's caching abstraction, backed by Redis
 * (auto-configured via spring-boot-starter-data-redis + application.yml).
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
