package org.quizstorage.director.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "async")
@Getter
@Setter
public class AsyncProperties {

    private int threadPoolSize;

    private int maxThreadPoolSize;

    private int threadQueueCapacity;

}
