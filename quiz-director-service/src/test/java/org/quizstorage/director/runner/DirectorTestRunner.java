package org.quizstorage.director.runner;

import org.quizstorage.director.DirectorServiceApp;
import org.quizstorage.director.configurations.WebSocketTestConfiguration;
import org.quizstorage.director.containers.MongodbContainerConfiguration;
import org.quizstorage.director.containers.RabbitMQContainerConfiguration;
import org.springframework.boot.SpringApplication;

public class DirectorTestRunner {


    public static void main(final String[] args) {
        SpringApplication
                .from(DirectorServiceApp::main)
                .with(
                        MongodbContainerConfiguration.class,
                        RabbitMQContainerConfiguration.class,
                        WebSocketTestConfiguration.class)
                .run(
                        "--spring.profiles.active=test",
                        "--containers.rabbitmq.config.enabled-plugins=rabbitmq_stomp,rabbitmq_web_stomp,rabbitmq_web_stomp_examples",
                        "--websocket.enable=true"
                );
    }

}
