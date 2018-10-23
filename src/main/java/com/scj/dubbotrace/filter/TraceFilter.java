package com.scj.dubbotrace.filter;

import com.scj.dubbotrace.RequestIDGenerator;
import com.scj.dubbotrace.TraceConstants;
import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

/**
 * 用于在web项目中，针对请求生成全局requestId
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-09-29 17:28
 */
public class TraceFilter implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestId =String.valueOf(RequestIDGenerator.getInstance().next());
        MDC.put(TraceConstants.RequestId,requestId);
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
