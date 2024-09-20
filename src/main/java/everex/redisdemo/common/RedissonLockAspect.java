package everex.redisdemo.common;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(everex.redisdemo.common.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);
        String lockKey = redissonLock.group() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.value());
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean lockable = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), TimeUnit.MILLISECONDS);
            if (!lockable) {
                System.out.println("lock 획득 실패");
                return false;
            }
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw e;
        } finally {
            try {
                lock.unlock();
            } catch (IllegalMonitorStateException e) {
                System.out.println("이미 잠금 해제 됨");
            }
        }
    }

}
