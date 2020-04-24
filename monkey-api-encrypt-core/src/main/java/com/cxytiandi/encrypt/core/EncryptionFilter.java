package com.cxytiandi.encrypt.core;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxytiandi.encrypt.algorithm.AesEncryptAlgorithm;
import com.cxytiandi.encrypt.algorithm.EncryptAlgorithm;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 数据加解密过滤器
 *
 * @author yinjihuan
 */
public class EncryptionFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(EncryptionFilter.class);

    private EncryptionConfig encryptionConfig;

    private EncryptAlgorithm encryptAlgorithm = new AesEncryptAlgorithm();

    public EncryptionFilter() {
        this.encryptionConfig = new EncryptionConfig();
    }

    public EncryptionFilter(EncryptionConfig config) {
        this.encryptionConfig = config;
    }

    public EncryptionFilter(EncryptionConfig config, EncryptAlgorithm encryptAlgorithm) {
        this.encryptionConfig = config;
        this.encryptAlgorithm = encryptAlgorithm;
    }

    public EncryptionFilter(String key) {
        EncryptionConfig config = new EncryptionConfig();
        config.setKey(key);
        this.encryptionConfig = config;
    }

    public EncryptionFilter(String key, List<String> responseEncryptUriList, List<String> requestDecryptUriList,
                            String responseCharset, boolean debug) {
        this.encryptionConfig = new EncryptionConfig(key, responseEncryptUriList, requestDecryptUriList, responseCharset, debug);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        logger.debug("RequestURI: {}", uri);

        // 调试模式不加解密
        if (encryptionConfig.isDebug()) {
            chain.doFilter(req, resp);
            return;
        }

        boolean decryptionStatus = this.contains(encryptionConfig.getRequestDecryptUriList(), uri, req.getMethod());
        boolean encryptionStatus = this.contains(encryptionConfig.getResponseEncryptUriList(), uri, req.getMethod());
        boolean decryptionIgnoreStatus = this.contains(encryptionConfig.getRequestDecryptUriIgnoreList(), uri, req.getMethod());
        boolean encryptionIgnoreStatus = this.contains(encryptionConfig.getResponseEncryptUriIgnoreList(), uri, req.getMethod());

        // 没有配置具体加解密的URI默认全部都开启加解密
        if (CollectionUtils.isEmpty(encryptionConfig.getRequestDecryptUriList())
                && CollectionUtils.isEmpty(encryptionConfig.getResponseEncryptUriList())) {
            decryptionStatus = true;
            encryptionStatus = true;
        }

        // 接口在忽略加密列表中
        if (encryptionIgnoreStatus) {
            encryptionStatus = false;
        }

        // 接口在忽略解密列表中
        if (decryptionIgnoreStatus) {
            decryptionStatus = false;
        }

        // 没有加解密操作
        if (!decryptionStatus && !encryptionStatus) {
            chain.doFilter(req, resp);
            return;
        }

        EncryptionResponseWrapper responseWrapper = null;
        EncryptionReqestWrapper requestWrapper = null;
        // 配置了需要解密才处理
        if (decryptionStatus) {
            requestWrapper = new EncryptionReqestWrapper(req);
            processDecryption(requestWrapper, req);
        }

        if (encryptionStatus) {
            responseWrapper = new EncryptionResponseWrapper(resp);
        }

        // 同时需要加解密
        if (encryptionStatus && decryptionStatus) {
            chain.doFilter(requestWrapper, responseWrapper);
        } else if (encryptionStatus) {
            // 只需要响应加密
            chain.doFilter(req, responseWrapper);
        } else if (decryptionStatus) {
            // 只需要请求解密
            chain.doFilter(requestWrapper, resp);
        }

        // 配置了需要加密才处理
        if (encryptionStatus) {
            String responseData = responseWrapper.getResponseData();
            writeEncryptContent(responseData, response);
        }

    }

    /**
     * 请求解密处理
     *
     * @param requestWrapper
     * @param req
     */
    private void processDecryption(EncryptionReqestWrapper requestWrapper, HttpServletRequest req) {
        String requestData = requestWrapper.getRequestData();
        String uri = req.getRequestURI();
        logger.debug("RequestData: {}", requestData);
        try {
            if (!StringUtils.endsWithIgnoreCase(req.getMethod(), RequestMethod.GET.name())) {
                String decryptRequestData = encryptAlgorithm.decrypt(requestData, encryptionConfig.getKey());
                logger.debug("DecryptRequestData: {}", decryptRequestData);
                requestWrapper.setRequestData(decryptRequestData);
            }

            // url参数解密
            Map<String, String> paramMap = new HashMap<>();
            Enumeration<String> parameterNames = req.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String prefixUri = req.getMethod().toLowerCase() + ":" + uri;
                if (encryptionConfig.getRequestDecryptParams(prefixUri).contains(paramName)) {
                    String paramValue = req.getParameter(paramName);
                    String decryptParamValue = encryptAlgorithm.decrypt(paramValue, encryptionConfig.getKey());
                    paramMap.put(paramName, decryptParamValue);
                }
            }
            requestWrapper.setParamMap(paramMap);
        } catch (Exception e) {
            logger.error("请求数据解密失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出加密内容
     *
     * @param responseData
     * @param response
     * @throws IOException
     */
    private void writeEncryptContent(String responseData, ServletResponse response) throws IOException {
        logger.debug("ResponseData: {}", responseData);
        ServletOutputStream out = null;
        try {
            responseData = encryptAlgorithm.encrypt(responseData, encryptionConfig.getKey());
            logger.debug("EncryptResponseData: {}", responseData);
            response.setContentLength(responseData.length());
            response.setCharacterEncoding(encryptionConfig.getResponseCharset());
            out = response.getOutputStream();
            out.write(responseData.getBytes(encryptionConfig.getResponseCharset()));
        } catch (Exception e) {
            logger.error("响应数据加密失败", e);
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    private boolean contains(List<String> list, String uri, String methodType) {
        if (list.contains(uri)) {
            return true;
        }
        String prefixUri = methodType.toLowerCase() + ":" + uri;
        logger.debug("contains uri: {}", prefixUri);
        if (list.contains(prefixUri)) {
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
