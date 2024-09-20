package everex.redisdemo.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
    String group();
    String value();
    long waitTime() default 10000L;
    long leaseTime() default 1000L;
}
