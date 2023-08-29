package org.quizstorage.director.security;

import lombok.RequiredArgsConstructor;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.exceptions.UserWebSocketTokenNotFoundException;
import org.quizstorage.director.services.UserWebSocketTokenDataService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationTokenProvider implements AuthenticationProvider {

    private final UserWebSocketTokenDataService tokenDataService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WebSocketAuthenticationToken token = (WebSocketAuthenticationToken) authentication;
        UUID tokenId = token.tokenId();
        return tokenDataService.findAndRemoveByTokenId(tokenId)
                .map(this::toQuizUserAuthenticationToken)
                .orElseThrow(() -> new UserWebSocketTokenNotFoundException(tokenId));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WebSocketAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private QuizUserAuthenticationToken toQuizUserAuthenticationToken(UserWebSocketTokenData tokenData) {
        QuizUser quizUser = toQuizUser(tokenData);
        Set<SimpleGrantedAuthority> authorities = tokenData.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new QuizUserAuthenticationToken(tokenData.getTokenId(), quizUser, authorities);
    }

    private QuizUser toQuizUser(UserWebSocketTokenData tokenData) {
        return new QuizUser(tokenData.getUserId(), tokenData.getName(), new HashSet<>(tokenData.getRoles()));
    }

}
