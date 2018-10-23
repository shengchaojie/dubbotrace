package com.scj.dubbotrace.filter;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.scj.dubbotrace.LogVo;
import com.scj.dubbotrace.RequestIDGenerator;
import com.scj.dubbotrace.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.slf4j.MDC;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-09-29 14:03
 */
@Slf4j
public abstract class AbstractLogFilter implements Filter {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private static final Pattern pattern = Pattern.compile("(.*\\.js$)||(.*\\.css$)||(.*\\.png$)||(.*\\.jpg$)||(.*\\.ico$)||(.*\\.gif$)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        try {
            if (request != null) {
                servletRequest = new BodyReaderHttpServletRequestWrapper(servletRequest);
            }
            /**
             * 生成全局ID，放到MDC中去
             */
            String requestID = MDC.get(TraceConstants.RequestId);
            if (requestID == null) {
                requestID = String.valueOf(RequestIDGenerator.getInstance().next());
            }
            LogVo logVo = new LogVo();
            logVo.setRequestId(requestID);
            logVo.setRequestMethod(servletRequest.getMethod());
            //得到当前请求的contextPath
            String contextPath = urlPathHelper.getContextPath(servletRequest);
            //获取请求Url,去除ContextPath
            String requestUri = servletRequest.getRequestURI();
            if (StringUtils.isNotEmpty(contextPath) && requestUri.indexOf(contextPath) == 0) {
                requestUri = requestUri.substring(contextPath.length());
            }
            logVo.setRequestUrl(requestUri);
            logVo.setContentType(servletRequest.getContentType());
            //获取请求body
            //只有post请求才有
            StringBuilder bodyStringBuilder = new StringBuilder();
            if ("GET".equals(logVo.getRequestMethod())) {
                bodyStringBuilder.append(servletRequest.getQueryString());
            } else if (logVo.getContentType() != null && logVo.getContentType().contains(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
                //content-type居然能为空。。。
                Enumeration<String> enumeration = servletRequest.getParameterNames();
                bodyStringBuilder.append("{");
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    String value = servletRequest.getParameter(key);
                    bodyStringBuilder.append("\"").append(key).append("\":\"").append(value).append("\",");
                }
                if (bodyStringBuilder.length() > 1) {
                    bodyStringBuilder.deleteCharAt(bodyStringBuilder.length() - 1);
                }
                bodyStringBuilder.append("}");
            } else if (logVo.getContentType() != null && "POST".equals(logVo.getRequestMethod()) && logVo.getContentType().contains(ContentType.APPLICATION_JSON.getMimeType())) {
                try {
                    BufferedReader br = servletRequest.getReader();
                    String line;
                    while ((line = br.readLine()) != null) {
                        bodyStringBuilder.append(line);
                    }
                } catch (Exception ex) {
                    log.error("读取请求失败", ex);
                }
            } else {
                log.info("该请求类型忽略");
            }
            logVo.setRequestBody(bodyStringBuilder.toString());
            logVo.setHost(servletRequest.getHeader("Host"));
            //通过从nginx X-Forwarded-For头获取原始ip
            //需要nginx配置
            String ip = servletRequest.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip)) {
                ip = servletRequest.getRemoteAddr();
            } else {
                String[] ips = ip.split(",");
                ip = ips[0];
            }
            logVo.setIp(ip);
            logVo.setOperateTime(new Date());
            resolveUserIdentify(logVo);

            log.info("Request:{}", JSON.toJSONString(logVo));

            chain.doFilter(servletRequest, response);
        }catch (Exception ex){
            log.error("日志拦截器发生异常",ex);
            chain.doFilter(servletRequest, response);
        }
    }

    private void resolveUserIdentify(LogVo logVo){
        String identify = getUserIdentify();
        if(!(identify == null || identify.length() == 0)){
            logVo.setUserIdentify(identify);
            MDC.put(TraceConstants.UserIdentify,identify);
        }
    }

    public abstract String getUserIdentify();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    public static void main(String[] args) {
        List<String> a = Lists.newArrayList("1","3","2");
        System.out.println(a.stream().filter(b->b=="2").collect(Collectors.toList()));
    }
}
