    package hello.hello_spring.common;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.github.benmanes.caffeine.cache.Cache;
    import com.github.benmanes.caffeine.cache.Caffeine;
    import io.github.bucket4j.Bandwidth;
    import io.github.bucket4j.Bucket;
    import io.github.bucket4j.Bucket4j;
    import io.github.bucket4j.Refill;
    import org.aspectj.lang.ProceedingJoinPoint;
    import org.aspectj.lang.annotation.Around;
    import org.aspectj.lang.annotation.Aspect;
    import org.springframework.stereotype.Component;

    import java.time.Duration;
    import java.util.Map;
    import java.util.concurrent.ConcurrentHashMap;
    import java.util.concurrent.TimeUnit;

    @Aspect
    @Component
    public class PreventDuplicateRequestAspect {

        private final ObjectMapper objectMapper = new ObjectMapper();

        // TTL 10초 설정된 캐시
        private final Cache<String, Bucket> requestBuckets = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .maximumSize(10_000)
                .build();

        @Around("@annotation(hello.hello_spring.aop.PreventDuplicateRequest)")
        public Object checkDuplicateRequest(ProceedingJoinPoint joinPoint) throws Throwable {
            String key = generateRequestHashKey(joinPoint);

            Bucket bucket = requestBuckets.get(key, k -> createOneTimeBucket());

            if (bucket.tryConsume(1)) {
                return joinPoint.proceed(); // 요청 허용
            } else {
                throw new IllegalStateException("동일한 요청이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
        }

        private Bucket createOneTimeBucket() {
            Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(1)));
            return Bucket4j.builder().addLimit(limit).build();
        }

        private String generateRequestHashKey(ProceedingJoinPoint joinPoint) {
            try {
                StringBuilder sb = new StringBuilder(joinPoint.getSignature().toShortString());

                for (Object arg : joinPoint.getArgs()) {
                    if (arg == null || isIgnorableType(arg)) continue;
                    String json = objectMapper.writeValueAsString(arg);
                    sb.append(":").append(json.hashCode());
                }

                return sb.toString();
            } catch (Exception e) {
                throw new RuntimeException("요청 해시 생성 실패", e);
            }
        }

        private boolean isIgnorableType(Object arg) {
            return arg instanceof jakarta.servlet.ServletRequest
                    || arg instanceof jakarta.servlet.ServletResponse
                    || arg instanceof org.springframework.validation.BindingResult;
        }
    }