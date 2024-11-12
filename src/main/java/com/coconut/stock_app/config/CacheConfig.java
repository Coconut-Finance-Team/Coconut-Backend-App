 package com.coconut.stock_app.config;

 import org.springframework.cache.annotation.EnableCaching;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.data.redis.cache.RedisCacheConfiguration;
 import org.springframework.data.redis.cache.RedisCacheManager;
 import org.springframework.data.redis.connection.RedisConnectionFactory;
 import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
 import org.springframework.data.redis.serializer.RedisSerializationContext;

 import java.time.Duration;

 @Configuration
 @EnableCaching
 public class CacheConfig {

     @Bean
     public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
         RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                 .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                 .entryTtl(Duration.ofMinutes(30)); // 캐시 유효 기간 설정

         return RedisCacheManager.builder(connectionFactory)
                 .cacheDefaults(config)
                 .build();
     }
 }

