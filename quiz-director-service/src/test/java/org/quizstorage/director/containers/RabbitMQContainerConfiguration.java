package org.quizstorage.director.containers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableSet;

@TestConfiguration(proxyBeanMethods = false)
public class RabbitMQContainerConfiguration {

    @Bean
    @ServiceConnection
    @RestartScope
    @SuppressWarnings("resource")
    RabbitMQContainer rabbitMqTestContainer(RabbitMQContainerProperties properties) {
        RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(properties.imageName)
                .withExposedPorts(properties.exposedPorts)
                .withPluginsEnabled(properties.enabledPlugins)
                .withUser(properties.userName, properties.password, ImmutableSet.of("administrator"))
                .withPermission("/", "admin", ".*", ".*", ".*");
        return rabbitMQContainer;
    }

    @Component
    static class RabbitMQContainerProperties {

        @Value("${containers.rabbitmq.config.image-name:rabbitmq:3.12-management}")
        private String imageName;

        @Value("${containers.rabbitmq.config.exposed-ports:5672,15672,61613,15692,15670,15674}")
        private Integer[] exposedPorts;

        @Value("${containers.rabbitmq.config.enabled-plugins:rabbitmq_management}")
        private String[] enabledPlugins;

        @Value("${containers.rabbitmq.config.username:admin}")
        private String userName;

        @Value("${containers.rabbitmq.config.password:admin}")
        private String password;

    }

}
