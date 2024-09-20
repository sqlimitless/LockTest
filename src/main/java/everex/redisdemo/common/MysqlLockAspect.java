package everex.redisdemo.common;

import everex.redisdemo.repo.LockRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class MysqlLockAspect {

    private final LockRepository lockRepository;

    @Around("@annotation(everex.redisdemo.common.MysqlLock)")
    public Object redissonLockNewTran(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        MysqlLock mysqlLock = method.getAnnotation(MysqlLock.class);
        String lockKey = mysqlLock.group() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), mysqlLock.value());
        try {
            boolean lockResult = lockRepository.getLock(lockKey, mysqlLock.waitTime());

            if (lockResult) {
                return joinPoint.proceed();
            } else {
                throw new RuntimeException("락을 획득하지 못했습니다.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return null;
        } finally {
            lockRepository.releaseLock(lockKey);
        }
    }
}
