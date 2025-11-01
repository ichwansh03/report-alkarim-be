package org.ichwan.service.impl;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RedisService {

    private final ValueCommands<String, Long> valueOps;
    private final KeyCommands<String> keyOps;

    public RedisService(RedisDataSource dataSource) {
        this.valueOps = dataSource.value(Long.class);
        this.keyOps = dataSource.key();
    }

    public boolean allowed(String key, long limit, long windowInSeconds) {
        String redisKey = "rate_limiter:" + key;
        long count = valueOps.incrby(redisKey, 1);
        if (count == 1) {
            keyOps.expire(redisKey, windowInSeconds);
        }
        return count <= limit;
    }
}
