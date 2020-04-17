package com.cxytiandi.encrypt.springboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *    忽略解密注解
* 
* <p>加了此注解的接口将不进行数据解密操作<p>
* <p>适用于全局开启加解密操作，但是想忽略某些接口场景<p>
* 
* @author yinjihuan
* 
* @about http://cxytiandi.com/about
*
*/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptIgnore {

	String value() default "";
	
}
