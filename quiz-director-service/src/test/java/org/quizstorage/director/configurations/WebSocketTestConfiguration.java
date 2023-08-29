package org.quizstorage.director.configurations;

import com.sun.security.auth.UserPrincipal;
import org.quizstorage.director.utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.RabbitMQContainer;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@TestConfiguration(proxyBeanMethods = false)
public class WebSocketTestConfiguration {

    public static String USER_ID = TestData.QUIZ_USER.id();

    public static UUID USER_TOKEN_ID = UUID.fromString("f222682c-f1fe-4990-9bb5-a85402cba482");

    @Bean
    WebSocketConnectionDetails webSocketConnectionDetails(RabbitMQContainer container,
                                                          RabbitConnectionDetails connectionDetails) {
        return new WebSocketConnectionDetails(
                container.getMappedPort(61613),
                connectionDetails.getFirstAddress().host(),
                container.getAdminUsername(),
                container.getAdminPassword(),
                container.getAdminUsername(),
                container.getAdminPassword()
        );
    }

    @Component
    public static class Properties {

        @Value("${server.port}")
        private int serverPort;

        @Value("${security.auth.headers.id-header-name}")
        private String userIdHeaderName;

        @Value("${security.auth.headers.web-socket-token-id-header-name}")
        private String webSocketTokenIdHeaderName;

        public int getServerPort() {
            return serverPort;
        }

        public String getUserIdHeaderName() {
            return userIdHeaderName;
        }

        public String getWebSocketTokenIdHeaderName() {
            return webSocketTokenIdHeaderName;
        }
    }

    @Primary
    @Component
    @ConditionalOnProperty(prefix = "security.auth.websocket.test-interceptor", name = "enable", havingValue = "true")
    public static class TestAuthenticationChannelInterceptor implements ChannelInterceptor {

        @Autowired
        private Properties properties;

        public TestAuthenticationChannelInterceptor() {
            System.out.println("CREATED!!!!!!!");
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            Optional.ofNullable(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class))
                    .ifPresent(this::authenticate);
            return message;
        }

        private void authenticate(StompHeaderAccessor headerAccessor) {
            if (Objects.equals(StompCommand.CONNECT, headerAccessor.getCommand())) {
                authenticateByTokenId(headerAccessor);
            }
        }

        private void authenticateByTokenId(StompHeaderAccessor headerAccessor) {
            Optional.ofNullable(headerAccessor.getFirstNativeHeader(properties.getWebSocketTokenIdHeaderName()))
                    .filter(tokenId -> Objects.equals(tokenId, USER_TOKEN_ID.toString()))
                    .map(token -> new UserPrincipal(USER_ID))
                    .ifPresent(headerAccessor::setUser);
        }

    }

}
