package hello.hello_spring.common;

import io.github.bucket4j.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Component
public class IpRateLimiter {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    public Bucket resolveBucket(String ip) {
        return cache.get(ip, k -> newBucket());
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.simple(10, Duration.ofSeconds(1)); //
        return Bucket4j.builder().addLimit(limit).build();
    }
}

