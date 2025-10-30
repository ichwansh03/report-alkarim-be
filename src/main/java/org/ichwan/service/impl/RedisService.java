package org.ichwan.service.impl;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisService {

    private final ValueCommands<String, Long> valueCommands;
    private final KeyCommands<String> keyCommands;

    public RedisService(RedisDataSource dataSource) {
        this.valueCommands = dataSource.value(Long.class);
        this.keyCommands = dataSource.key();
    }

    public boolean allowed(String key, long limit, long windowInSeconds) {
        String redisKey = "rate_limiter:" + key;
        long count = valueCommands.incrby(redisKey, 1);
        if (count == 1) {
            keyCommands.expire(redisKey, windowInSeconds);
        }
        return count <= limit;
    }
}
