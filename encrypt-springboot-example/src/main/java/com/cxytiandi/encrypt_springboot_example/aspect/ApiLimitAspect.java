package com.cxytiandi.encrypt_springboot_example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 使用切面模拟注解扫描不到问题
 * @author yinjihuan
 *
 */
//@Component
@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ApiLimitAspect {

	@Around("execution(* com.cxytiandi.encrypt_springboot_example.controller.*.*(..))")
	public Object around(ProceedingJoinPoint joinPoint) {
		try {
			return joinPoint.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}