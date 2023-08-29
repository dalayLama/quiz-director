package org.quizstorage.director.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.messaging.util.matcher.MessageMatcher;
import org.springframework.security.messaging.util.matcher.SimpMessageTypeMatcher;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AuthenticationChannelInterceptor implements ChannelInterceptor {

    private final QuizUserTokenGenerator tokenGenerator;

    private final AuthenticationManager authenticationManager;

    private final MessageMatcher<Object> matcher = new SimpMessageTypeMatcher(SimpMessageType.CONNECT);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        Optional.of(message)
                .filter(matcher::matches)
                .map(m -> MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class))
                .ifPresent(this::authenticate);
        return message;
    }

    private void authenticate(StompHeaderAccessor headerAccessor) {
        tokenGenerator.createByWebSocketTokenId(headerAccessor::getFirstNativeHeader)
                .map(authenticationManager::authenticate)
                .ifPresent(headerAccessor::setUser);
    }

}