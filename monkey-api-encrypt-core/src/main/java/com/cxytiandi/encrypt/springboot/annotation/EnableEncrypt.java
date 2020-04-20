package com.cxytiandi.encrypt.springboot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

import com.cxytiandi.encrypt.springboot.autoconfigure.EncryptAutoConfiguration;

/**
 * 启用加密Starter
 * 
 * <p>在Spring Boot启动类上加上此注解
 * 
 * @author yinjihuan http://cxytiandi.com/about
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EncryptAutoConfiguration.class})
public @interface EnableEncrypt {

}
