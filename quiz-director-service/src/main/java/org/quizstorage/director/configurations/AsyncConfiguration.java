package org.quizstorage.director.configurations;

import lombok.RequiredArgsConstructor;
import org.quizstorage.director.configurations.properties.AsyncProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsyncConfiguration implements AsyncConfigurer {

    private final AsyncProperties properties;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setQueueCapacity(properties.getThreadQueueCapacity());
        executor.setMaxPoolSize(properties.getMaxThreadPoolSize());
        executor.setCorePoolSize(properties.getThreadPoolSize());
        executor.initialize();
        return executor;
    }

}
