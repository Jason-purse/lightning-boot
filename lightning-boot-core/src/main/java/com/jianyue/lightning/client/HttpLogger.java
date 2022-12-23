package com.jianyue.lightning.client;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private final static Logger logger = LoggerFactory.getLogger(HttpLoggingInterceptor.class);
    @Override
    public void log(String message) {
        logger.info(message);
    }
}
