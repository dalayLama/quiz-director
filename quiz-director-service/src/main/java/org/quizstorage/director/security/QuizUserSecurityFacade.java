package org.quizstorage.director.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class QuizUserSecurityFacade {

    private final QuizUserTokenGenerator tokenGenerator;

    private final AuthenticationManager authenticationManager;

    public Optional<Authentication> authenticateByHeaders(Function<String, String> userFieldsProvider) {
        return tokenGenerator.create(userFieldsProvider).map(this::authenticate);
    }

    public Optional<Authentication> authenticateByWebSocketTokenId(Function<String, String> tokenIdProvider) {
        return tokenGenerator.createByWebSocketTokenId(tokenIdProvider).map(this::authenticate);
    }

    public Optional<Authentication> authenticateByUserId(String userId) {
        return tokenGenerator.createByUserId(userId).map(this::authenticate);
    }

    private Authentication authenticate(Authentication token) {
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(emptyContext);
        return authentication;
    }

}
