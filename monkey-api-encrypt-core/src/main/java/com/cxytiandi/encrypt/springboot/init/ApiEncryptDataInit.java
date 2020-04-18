package com.cxytiandi.encrypt.springboot.init;

import java.lang.reflect.Method;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cxytiandi.encrypt.springboot.HttpMethodTypePrefixConstant;
import com.cxytiandi.encrypt.springboot.annotation.Decrypt;
import com.cxytiandi.encrypt.springboot.annotation.DecryptIgnore;
import com.cxytiandi.encrypt.springboot.annotation.Encrypt;
import com.cxytiandi.encrypt.springboot.annotation.EncryptIgnore;

public class ApiEncryptDataInit implements ApplicationContextAware {
	
	private Logger logger = LoggerFactory.getLogger(ApiEncryptDataInit.class);
	
	/**
	 * 需要对响应内容进行加密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	public static List<String> responseEncryptUriList = new ArrayList<>();
	
	/**
	 * 需要对请求内容进行解密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	public static List<String> requestDecyptUriList = new ArrayList<>();
    
	/**
	 * 忽略加密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	public static List<String> responseEncryptUriIgnoreList = new ArrayList<>();
	
	/**
	 * 忽略对请求内容进行解密的接口URI<br>
	 * 比如：/user/list<br>
	 * 不支持@PathVariable格式的URI
	 */
	public static List<String> requestDecyptUriIgnoreList = new ArrayList<String>();

	/**
	 * Url参数需要解密的配置
	 * 比如：/user/list?name=加密内容<br>
	 * 格式：Key API路径  Value 需要解密的字段
	 * 示列：/user/list => [name,age]
	 */
	public static Map<String, List<String>> requestDecyptParamMap = new HashMap<>();

	private String contextPath;
	
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
    	this.contextPath = ctx.getEnvironment().getProperty("server.servlet.context-path");
		Map<String, Object> beanMap = ctx.getBeansWithAnnotation(Controller.class);
        initData(beanMap);
        initRequestDecyptParam(ctx.getEnvironment());
    }

	/**
	 * 初始化Url 参数解密配置
	 * @param environment
	 */
	private void initRequestDecyptParam(Environment environment) {
		for(Iterator it = ((AbstractEnvironment) environment).getPropertySources().iterator(); it.hasNext(); ) {
			PropertySource propertySource = (PropertySource) it.next();
			if (propertySource instanceof EnumerablePropertySource) {
				for(String name : ((EnumerablePropertySource)propertySource).getPropertyNames()) {
					if (name.startsWith("spring.encrypt.requestDecyptParam")) {
						String[] keys = name.split("\\.");
						String key = keys[keys.length - 1];
						String property = environment.getProperty(name);
						requestDecyptParamMap.put(key.replace("$", ":"), Arrays.asList(property.split(",")));
					}
				}
			}
		}
	}

	private void initData(Map<String, Object> beanMap) {
		if (beanMap != null) {
            for (Object bean : beanMap.values()) {
                Class<?> clz = bean.getClass();
                Method[] methods = clz.getMethods();
                for (Method method : methods) {
                	Encrypt encrypt = AnnotationUtils.findAnnotation(method, Encrypt.class);
                	if (encrypt != null) {
                		// 注解中的URI优先级高
                    	String uri = encrypt.value();
                    	if (!StringUtils.hasText(uri)) {
                    		uri = getApiUri(clz, method);
						}
                        logger.debug("Encrypt URI: {}", uri);
                        responseEncryptUriList.add(uri);
                	}
                	Decrypt decrypt = AnnotationUtils.findAnnotation(method, Decrypt.class);
                    if (decrypt != null) {
                    	String uri = decrypt.value();
                    	if (!StringUtils.hasText(uri)) {
                    		uri = getApiUri(clz, method);
						}

                    	String decyptParam = decrypt.decyptParam();
						if (StringUtils.hasText(decyptParam)) {
							requestDecyptParamMap.put(uri, Arrays.asList(decyptParam.split(",")));
						}

                        logger.debug("Decrypt URI: {}", uri);
                        requestDecyptUriList.add(uri);
                    }
                    EncryptIgnore encryptIgnore = AnnotationUtils.findAnnotation(method, EncryptIgnore.class);
                	if (encryptIgnore != null) {
                		// 注解中的URI优先级高
                    	String uri = encryptIgnore.value();
                    	if (!StringUtils.hasText(uri)) {
                    		uri = getApiUri(clz, method);
						}
                        logger.debug("EncryptIgnore URI: {}", uri);
                        responseEncryptUriIgnoreList.add(uri);
                	}
                	DecryptIgnore decryptIgnore = AnnotationUtils.findAnnotation(method, DecryptIgnore.class);
                    if (decryptIgnore != null) {
                    	String uri = decryptIgnore.value();
                    	if (!StringUtils.hasText(uri)) {
                    		uri = getApiUri(clz, method);
						}
                        logger.debug("DecryptIgnore URI: {}", uri);
                        requestDecyptUriIgnoreList.add(uri);
                    }
                }
            }
        }
	}
    
    private String getApiUri(Class<?> clz, Method method) {
    	String methodType = "";
        StringBuilder uri = new StringBuilder();
        
        RequestMapping reqMapping = AnnotationUtils.findAnnotation(clz, RequestMapping.class);
        if (reqMapping != null) {
        	uri.append(formatUri(reqMapping.value()[0]));
		}
        
        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
        
        if (getMapping != null) {
        	methodType = HttpMethodTypePrefixConstant.GET;
            uri.append(formatUri(getMapping.value()[0]));
            
        } else if (postMapping != null) {
        	methodType = HttpMethodTypePrefixConstant.POST;
            uri.append(formatUri(postMapping.value()[0]));
            
        } else if (putMapping != null) {
        	methodType = HttpMethodTypePrefixConstant.PUT;
            uri.append(formatUri(putMapping.value()[0]));
            
        } else if (deleteMapping != null) {
        	methodType = HttpMethodTypePrefixConstant.DELETE;
            uri.append(formatUri(deleteMapping.value()[0]));
            
        } else if (requestMapping != null) {
        	RequestMethod m = requestMapping.method()[0];
        	methodType = m.name().toLowerCase() + ":";
            uri.append(formatUri(requestMapping.value()[0]));
            
        } 
        
        if (StringUtils.hasText(this.contextPath) && !"/".equals(this.contextPath)) {
        	 return methodType + this.contextPath + uri.toString();
		}
        
        return methodType + uri.toString();
    }
    
    private String formatUri(String uri) {
    	if (uri.startsWith("/")) {
			return uri;
		}
    	return "/" + uri;
    }
}
