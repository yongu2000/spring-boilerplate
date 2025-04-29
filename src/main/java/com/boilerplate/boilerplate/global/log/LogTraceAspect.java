package com.boilerplate.boilerplate.global.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("execution(* com.boilerplate.boilerplate.domain..controller..*(..))" +
        "|| execution(* com.boilerplate.boilerplate.domain..service..*(..))" +
        "|| execution(* com.boilerplate.boilerplate.domain..repository..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;

        // log.info("target={}", joinPoint.getTarget()); //실제 호출 대상
        // log.info("getArgs={}", joinPoint.getArgs()); //전달인자
        // log.info("getSignature={}", joinPoint.getSignature()); //join point 시그니처

        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);

            //로직 호출
            Object result = joinPoint.proceed();

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        } finally {
            logTrace.end(status);
        }
    }
}
