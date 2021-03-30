package com.cxytiandi.encrypt.util;


import com.cxytiandi.encrypt.springboot.HttpMethodTypePrefixConstant;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

public class RequestUriUtils {

    private static String separator = "/";

    public static String getApiUri(Class<?> clz, Method method, String contextPath) {
        String methodType = "";
        StringBuilder uri = new StringBuilder();

        RequestMapping reqMapping = AnnotationUtils.findAnnotation(clz, RequestMapping.class);
        if (reqMapping != null && reqMapping.value() != null && reqMapping.value().length > 0) {
            uri.append(formatUri(reqMapping.value()[0]));
        }


        GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
        PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
        DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);

        if (getMapping != null && getMapping.value() != null && getMapping.value().length > 0) {
            methodType = HttpMethodTypePrefixConstant.GET;
            uri.append(formatUri(getMapping.value()[0]));

        } else if (postMapping != null && postMapping.value() != null && postMapping.value().length > 0) {
            methodType = HttpMethodTypePrefixConstant.POST;
            uri.append(formatUri(postMapping.value()[0]));

        } else if (putMapping != null && putMapping.value() != null && putMapping.value().length > 0) {
            methodType = HttpMethodTypePrefixConstant.PUT;
            uri.append(formatUri(putMapping.value()[0]));

        } else if (deleteMapping != null && deleteMapping.value() != null && deleteMapping.value().length > 0) {
            methodType = HttpMethodTypePrefixConstant.DELETE;
            uri.append(formatUri(deleteMapping.value()[0]));

        } else if (requestMapping != null && requestMapping.value() != null && requestMapping.value().length > 0) {
            RequestMethod requestMethod = RequestMethod.GET;
            if (requestMapping.method().length > 0) {
                requestMethod = requestMapping.method()[0];
            }

            methodType = requestMethod.name().toLowerCase() + ":";
            uri.append(formatUri(requestMapping.value()[0]));

        }

        if (StringUtils.hasText(contextPath) && !separator.equals(contextPath)) {
            if (contextPath.endsWith(separator)) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            return methodType + contextPath + uri.toString();
        }

        return methodType + uri.toString();
    }

    private static String formatUri(String uri) {
        if (uri.startsWith(separator)) {
            return uri;
        }
        return separator + uri;
    }
}
