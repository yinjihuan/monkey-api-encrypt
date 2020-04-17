package com.cxytiandi.mvc.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.cxytiandi.encrypt.core.EncryptionConfig;
import com.cxytiandi.encrypt.core.EncryptionFilter;

public class ApiEncryptionFilter implements Filter {

	EncryptionFilter filter = null;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		EncryptionConfig config = new EncryptionConfig();
		config.setKey("abcdef0123456789");
		config.setRequestDecyptUriList(Arrays.asList("/save"));
		config.setResponseEncryptUriList(Arrays.asList("/encryptEntity"));
		filter = new EncryptionFilter(config);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		filter.doFilter(request, response, chain);
	}

	@Override
	public void destroy() {
		
	}

}
