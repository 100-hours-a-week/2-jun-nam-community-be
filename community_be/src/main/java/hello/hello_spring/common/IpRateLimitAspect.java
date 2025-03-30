package hello.hello_spring.common;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.*;

@Aspect
@Component
public class IpRateLimitAspect {

    private final IpRateLimiter limiter = new IpRateLimiter();

    @Around("execution(* hello.hello_spring.controller..*(..)) && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object limitApiByIp(ProceedingJoinPoint joinPoint) throws Throwable {
        String ip = extractClientIp();

        Bucket bucket = limiter.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            return joinPoint.proceed(); // 허용된 요청
        } else {
            throw new IllegalStateException("해당 IP에서 너무 많은 요청을 보냈습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    private String extractClientIp() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();

            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader != null) {
                return xfHeader.split(",")[0]; // 프록시 거친 IP 추출
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}