package com.scj.dubbotrace;

/**
 * 全局请求ID生成器
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-09-29 14:55
 */
public class RequestIDGenerator {

    private SnowFlake snowFlake =new SnowFlake(1,1);

    public Long  next(){
        return snowFlake.nextId();
    }

    private static class RequestIDGeneratorHolder{
        private static RequestIDGenerator instance = new RequestIDGenerator();
    }

    public static RequestIDGenerator getInstance(){
        return RequestIDGeneratorHolder.instance;
    }

}
