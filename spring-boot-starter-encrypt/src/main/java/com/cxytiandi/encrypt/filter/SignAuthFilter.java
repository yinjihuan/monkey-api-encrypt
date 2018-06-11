package com.cxytiandi.encrypt.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cxytiandi.encrypt.auto.EncryptProperties;
import com.cxytiandi.encrypt.util.AesEncryptUtils;
import com.cxytiandi.encrypt.util.JsonUtils;

/**
 * 请求签名验证过滤器<br>
 * 
 * 请求头中获取sign进行校验，判断合法性和是否过期<br>
 * 
 * sign=加密({参数：值, 参数2：值2, signTime:签名时间戳})
 * @author yinjihuan
 * 
 * @about http://cxytiandi.com/about
 *
 */
public class SignAuthFilter implements Filter {

	private EncryptProperties encryptProperties;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext context = filterConfig.getServletContext();  
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        encryptProperties = ctx.getBean(EncryptProperties.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setCharacterEncoding("UTF-8");
		String sign = req.getHeader("sign");
		if (!StringUtils.hasText(sign)) {
			PrintWriter print = resp.getWriter();
			print.write("非法请求:缺少签名信息");
			return;
		}
		try {
			String decryptBody = AesEncryptUtils.aesDecrypt(sign, encryptProperties.getKey());
			Map<String, Object> signInfo = JsonUtils.getMapper().readValue(decryptBody, Map.class);
			Long signTime = (Long) signInfo.get("signTime");
			
			// 签名时间和服务器时间相差10分钟以上则认为是过期请求，此时间可以配置
			if ((System.currentTimeMillis() - signTime) > encryptProperties.getSignExpireTime() * 60000) {
				PrintWriter print = resp.getWriter();
				print.write("非法请求:已过期");
				return;
			}
			
			// POST请求只处理时间
			// GET请求处理参数和时间
			if(req.getMethod().equals(HttpMethod.GET.name())) {
				Set<String> paramsSet = signInfo.keySet();
				for (String key : paramsSet) {
					if (!"signTime".equals(key)) {
						String signValue = signInfo.get(key).toString();
						String reqValue = req.getParameter(key).toString();
						if (!signValue.equals(reqValue)) {
							PrintWriter print = resp.getWriter();
							print.write("非法请求:参数被篡改");
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			PrintWriter print = resp.getWriter();
			print.write("非法请求:" + e.getMessage());
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		
	}
	
}
