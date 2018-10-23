## dubbotrace
请求日志链路监控，针对请求或者Dubbo调用通过生成全局requestid，通过filter在各个环境传递这个requestid，实现在ELK通过requestid查询请求链路日志

## 如何使用
0.引入这个jar包  
由于Dubbo的spi机制，dubbo服务之间的requestId只要引用了这个jar包，会无侵入集成

1.对web项目配置TraceFilter  
用于针对每个http请求生成requestid，最好配置为第一个filter

2.设置日志打印格式
我们需要规范每个项目的日志输出格式
requestid存储在MDC中，如果是logback，通过%X输出，如下

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{RequestId}] [%X{UserIdentify}] [%thread] %-5level %logger{60} - %msg%n
```

通过logback的spi，我实现了LogbackAutoConfigGenerator，可以在项目没有logback.xml的情况下，默认采用我的配置。使用这个可以统一全局配置。其他日志框架应该也支持这个。

3.ELK日志收集
这部分自行实现，最终目的是在kibana通过requestid查询请求链路日志

