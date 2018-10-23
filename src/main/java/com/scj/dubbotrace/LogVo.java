package com.scj.dubbotrace;

import lombok.Data;

import java.util.Date;

@Data
public class LogVo {

    /**
     * 全局请求ID
     */
    private String requestId;

    /**
     * 请求方法 GET POST
     */
    private String requestMethod;

    /**
     * 请求体 POST请求才有
     */
    private String requestBody;

    /**
     * IP
     */
    private String ip;

    /**
     * 请求URl
     */
    private String requestUrl;


    private String contentType;

    /**
     * 域名
     */
    private String host;

    /**
     * 请求时间
     */
    private Date operateTime;

    /**
     * 用户识别标识
     */
    private String userIdentify;


    /**
     * 应用上下文
     */
    private String contextPath;
}
