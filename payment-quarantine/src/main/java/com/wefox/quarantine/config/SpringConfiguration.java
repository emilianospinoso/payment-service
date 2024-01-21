package com.wefox.quarantine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class SpringConfiguration implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}
