package org.quizstorage.director.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.quizstorage.director.components.ExternalBrokerUserDestinationResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@ConditionalOnProperty(prefix = "websocket", name = "enable", havingValue = "true")
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final ObjectMapper objectMapper;

    private final ChannelInterceptor channelInterceptor;

    private final WebSocketConnectionDetails webSocketConnectionDetails;

    private final ApplicationEventPublisher publisher;

    public WebSocketConfiguration(ObjectMapper objectMapper,
                                  ChannelInterceptor channelInterceptor,
                                  WebSocketConnectionDetails webSocketConnectionDetails,
                                  ApplicationEventPublisher publisher) {
        this.objectMapper = objectMapper;
        this.channelInterceptor = channelInterceptor;
        this.webSocketConnectionDetails = webSocketConnectionDetails;
        this.publisher = publisher;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/exchange")
                .setRelayHost(webSocketConnectionDetails.relayHost())
                .setRelayPort(webSocketConnectionDetails.relayPort())
                .setSystemLogin(webSocketConnectionDetails.systemLogin())
                .setSystemPasscode(webSocketConnectionDetails.systemPasscode())
                .setClientLogin(webSocketConnectionDetails.clientLogin())
                .setClientPasscode(webSocketConnectionDetails.clientPasscode());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }

    @Bean
    @Primary
    public UserDestinationResolver consistentUserDestinationResolver() {
        return new ExternalBrokerUserDestinationResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> myAuthorizationRules = AuthenticatedAuthorizationManager.authenticated();
        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(myAuthorizationRules);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(this.publisher);
        authz.setAuthorizationEventPublisher(publisher);
        registration.interceptors(channelInterceptor, new SecurityContextChannelInterceptor(), authz);
//        registration.interceptors(channelInterceptor);
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setTaskDecorator(DelegatingSecurityContextRunnable::new);
//        registration.taskExecutor(threadPoolTaskExecutor);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }

}
