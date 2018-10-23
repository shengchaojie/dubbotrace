package com.scj.dubbotrace.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

/**
 * 利用logback spi特性 在classpath下没有logback.xml配置时，默认采用这个类提供的配置
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-10-10 16:23
 */
public class LogbackAutoConfigGenerator extends ConfiguratorAdapter{

    /**
     * 需要通过环境变量配置项目名
     *
     */
    private static final String PROJECT_NAME = System.getProperty("projectName","ttms"+ UUID.randomUUID().toString());

    private static final String PROJECT_NAMESAPCE = System.getProperty("projectNameSpace","com.kuaihuoyun");

    private static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{RequestId}] [%thread] %-5level %logger{60} - %msg%n";

    private static final String LOG_CHARSET = "UTF-8";

    private static final String LOG_HOME ="/app/logs/"+PROJECT_NAME;

    @Override
    public void configure(LoggerContext loggerContext) {


        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender("console",loggerContext);
        RollingFileAppender<ILoggingEvent> otherAppender = createRollingFileAppender("common",false,loggerContext);
        RollingFileAppender<ILoggingEvent> errorAppender = createRollingFileAppender("error",true,loggerContext);
        RollingFileAppender<ILoggingEvent> bizAppender = createRollingFileAppender("biz",false,loggerContext);
        RollingFileAppender<ILoggingEvent> dubboAppender = createRollingFileAppender("dubbo",false,loggerContext);
        RollingFileAppender<ILoggingEvent> springAppender = createRollingFileAppender("spring",false,loggerContext);
        RollingFileAppender<ILoggingEvent> httpAppender = createRollingFileAppender("http",false,loggerContext);

        initLogger(loggerContext,Logger.ROOT_LOGGER_NAME,Level.INFO, Lists.newArrayList(errorAppender,otherAppender,consoleAppender));
        initLogger(loggerContext,PROJECT_NAMESAPCE,Level.INFO, Lists.newArrayList(errorAppender,bizAppender));
        initLogger(loggerContext,"com.alibaba.dubbo",Level.INFO, Lists.newArrayList(errorAppender,dubboAppender));
        initLogger(loggerContext,"org.springframework",Level.INFO, Lists.newArrayList(errorAppender,springAppender));

    }


    private void initLogger(LoggerContext loggerContext, String name,Level level, List<Appender<ILoggingEvent>> appenders){
        Logger logger = loggerContext.getLogger(name);
        logger.setLevel(level);
        appenders.stream().forEach(a->{
            logger.addAppender(a);
        });
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(String name,LoggerContext loggerContext){
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(loggerContext);
        ca.setName(name);
        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(loggerContext);
        pl.setPattern(LOG_PATTERN);
        pl.setCharset(Charset.forName(LOG_CHARSET));
        pl.start();

        ca.setEncoder(pl);
        ca.start();

        return ca;
    }

    private RollingFileAppender<ILoggingEvent> createRollingFileAppender(String name,boolean filterError,LoggerContext loggerContext){
        RollingFileAppender<ILoggingEvent> rfa = new RollingFileAppender<>();
        rfa.setContext(loggerContext);
        rfa.setName(name);
        rfa.setFile(LOG_HOME+"/"+PROJECT_NAME+"-"+name+".log");
        rfa.setAppend(false);

        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(loggerContext);
        pl.setPattern(LOG_PATTERN);
        pl.setCharset(Charset.forName(LOG_CHARSET));
        pl.start();

        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setContext(loggerContext);
        policy.setMaxHistory(30);
        policy.setFileNamePattern(LOG_HOME+"/"+PROJECT_NAME+"-"+name+"_%d{yyyy-MM-dd}.log");
        policy.setParent(rfa);
        policy.start();

        if(filterError){
            ThresholdFilter filter = new ThresholdFilter();
            filter.setLevel("ERROR");
            filter.start();
            rfa.addFilter(filter);
        }

        rfa.setEncoder(pl);
        rfa.setTriggeringPolicy(policy);
        rfa.start();

        return rfa;
    }

    public static void main(String[] args) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(LogbackAutoConfigGenerator.class);
        logger.info("123");
    }
}
