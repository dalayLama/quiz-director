package org.quizstorage.director.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class QuizUserAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return supports(authentication.getClass()) ? authentication : null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return QuizUserAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
