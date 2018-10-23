package com.scj.dubbotrace.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;

/**
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-10-10 16:27
 */
public class ConfiguratorAdapter implements Configurator{
    @Override
    public void configure(LoggerContext loggerContext) {

    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void addStatus(Status status) {

    }

    @Override
    public void addInfo(String msg) {

    }

    @Override
    public void addInfo(String msg, Throwable ex) {

    }

    @Override
    public void addWarn(String msg) {

    }

    @Override
    public void addWarn(String msg, Throwable ex) {

    }

    @Override
    public void addError(String msg) {

    }

    @Override
    public void addError(String msg, Throwable ex) {

    }
}
