package com.mftplus.mftonlinestoreee.core.logging;

import jakarta.interceptor.InvocationContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoggingInterceptorTest {

    private final LoggingInterceptor interceptor = new LoggingInterceptor();

    @Test
    void returnsWhatTheMethodReturns() throws Exception {
        InvocationContext context = context("findById");
        when(context.proceed()).thenReturn("result");

        assertEquals("result", interceptor.log(context));
        verify(context).proceed();
    }

    @Test
    void rethrowsTheSameException() throws Exception {
        InvocationContext context = context("create");
        IllegalStateException failure = new IllegalStateException("boom");
        when(context.proceed()).thenThrow(failure);

        Exception thrown = assertThrows(IllegalStateException.class, () -> interceptor.log(context));
        assertSame(failure, thrown);   // not swallowed, not wrapped: the mappers still see it
    }

    private InvocationContext context(String methodName) throws NoSuchMethodException {
        InvocationContext context = mock(InvocationContext.class);
        when(context.getMethod()).thenReturn(Sample.class.getMethod(methodName));
        return context;
    }

    public static class Sample {
        public String findById() { return null; }
        public String create() { return null; }
    }
}
