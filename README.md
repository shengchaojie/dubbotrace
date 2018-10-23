## dubbotrace
请求日志链路监控，针对请求或者Dubbo调用通过生成全局requestid，通过filter在各个环境传递这个requestid，实现在ELK通过requestid查询请求链路日志

## 如何使用
1.配置TraceFilter
用于针对每个http请求生成requestid

2.设置日志打印格式
requestid存储在MDC中，如果是logback，通过%x输出

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{RequestId}] [%X{UserIdentify}] [%thread] %-5level %logger{60} - %msg%n
```

通过logback的spi，我实现了LogbackAutoConfigGenerator，可以在项目没有logback.xml的情况下，默认采用我的配置。使用这个可以统一全局配置。其他日志框架应该也支持这个。

3.ELK解析
这部分自行实现，最终目的是通过requestid查询请求链路日志

