package com.mftplus.mftonlinestoreee.core.logging;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

@Loggable
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor {

    @AroundInvoke
    public Object log(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Logger log = LoggerFactory.getLogger(method.getDeclaringClass());
        long start = System.nanoTime();
        try {
            Object result = context.proceed();
            log.debug("{} finished in {} ms", method.getName(), elapsedMillis(start));
            return result;
        } catch (Exception exception) {
            log.warn("{} failed in {} ms: {}", method.getName(), elapsedMillis(start), exception.toString());
            throw exception;
        }
    }

    private long elapsedMillis(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
