package com.ecmspace.core.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.ecmspace")
public class AOPConfiguration {
	
	/*@Bean   
    public AOPLogger aopLogger(){
       return new AOPLogger();
    }*/

}
