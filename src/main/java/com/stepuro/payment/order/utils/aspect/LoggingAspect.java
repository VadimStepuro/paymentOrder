package com.stepuro.payment.order.utils.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    private final ObjectMapper mapper;

    public LoggingAspect(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Pointcut("@annotation(com.stepuro.payment.order.api.annotation.Loggable)")
    public void pointcut() {
    }


    @Before("pointcut()")
    public void logMethod(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        Map<String, Object> parameters = getParameters(joinPoint);

        try {
            logger.info("==> class name: {}, method name: {}, arguments: {} ",
                     className,
                    methodName,
                    mapper.writeValueAsString(parameters));
        }
        catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        logger.error("Exception {} in class: {}, in method: {}, with message = {}",
                e.getClass().getName(),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getMessage() != null ? e.getMessage() : "NULL");
    }

    @AfterReturning(pointcut = "pointcut()", returning = "entity")
    public void logMethodAfter(JoinPoint joinPoint, ResponseEntity<?> entity) {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();

        try {
            logger.info("<== class name: {}, method name: {}, retuning: {}",
                    className,
                    methodName,
                    mapper.writeValueAsString(entity));
        }
        catch (JsonProcessingException e) {
            logger.error("Error while converting", e);
        }
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        HashMap<String, Object> map = new HashMap<>();

        String[] parameterNames = signature.getParameterNames();

        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], joinPoint.getArgs()[i]);
        }

        return map;
    }
}
