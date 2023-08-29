package org.quizstorage.director.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.quizstoradge.director.dto.GameEventType;
import org.quizstorage.director.configurations.properties.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfiguration {

    private final RabbitMQProperties properties;

    @Bean
    public Queue finishedGamesQueue() {
        return new Queue(properties.getFinishedGamesQueue());
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(properties.getExchangeName());
    }

    @Bean
    public Binding finishedGamesBinding(Queue finishedGamesQueue, TopicExchange directExchange) {
        return BindingBuilder
                .bind(finishedGamesQueue)
                .to(directExchange)
                .with(GameEventType.FINISHED_GAME);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setExchange(properties.getExchangeName());
        return rabbitTemplate;
    }

    @ConditionalOnProperty(prefix = "websocket", name = "enable", havingValue = "true")
    @ConditionalOnMissingBean
    @Bean
    public WebSocketConnectionDetails webSocketConnectionDetails(
            @Value("${rabbitmq.stomp-port:61613}") int stompPort,
            RabbitConnectionDetails rabbitConnectionDetails) {
        return new WebSocketConnectionDetails(
                stompPort,
                rabbitConnectionDetails.getFirstAddress().host(),
                rabbitConnectionDetails.getUsername(),
                rabbitConnectionDetails.getPassword(),
                rabbitConnectionDetails.getUsername(),
                rabbitConnectionDetails.getPassword()
        );
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
