package org.quizstorage.director.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class RabbitMQProperties {

    private String exchangeName;

    @Value("${rabbitmq.queues.finished-games-queue}")
    private String finishedGamesQueue;

}
