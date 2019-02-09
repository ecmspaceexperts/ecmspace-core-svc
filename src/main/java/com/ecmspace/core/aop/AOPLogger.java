package com.ecmspace.core.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AOPLogger {
	
	/** Handle to the log file */
    private final Log log = LogFactory.getLog(getClass());


    @AfterReturning("execution(* com.ecmspace.core..*.*(..))")
    public void logMethodAccessAfter(JoinPoint joinPoint) {
       log.info("***** Completed: " + joinPoint.getSignature().getName() + " *****");
       // System.out.println("***** Completed: " + joinPoint.getSignature().getDeclaringTypeName()+" : "+ joinPoint.getSignature().getName() + " *****");
    }

    @Before("execution(* com.ecmspace.core..*.*(..))")
    public void logMethodAccessBefore(JoinPoint joinPoint) {
        log.info("***** Starting: " + joinPoint.getSignature().getName() + " *****");
       // System.out.println("***** Starting: " + joinPoint.getSignature().getDeclaringTypeName()+" : "+ joinPoint.getSignature().getName() + " *****");
        
    }

}
