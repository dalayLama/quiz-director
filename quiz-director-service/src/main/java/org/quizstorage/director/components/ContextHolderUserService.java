package org.quizstorage.director.components;

import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.security.QuizUserAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class ContextHolderUserService implements UserService {

    private static final String CURRENT_USER_IS_NOT_DEFINED_MESSAGE = "The current user is not defined";

    @Override
    public Optional<QuizUser> getAuthorizedUserData() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(c -> (QuizUserAuthenticationToken) c.getAuthentication())
                .map(QuizUserAuthenticationToken::getUserData);
    }

    @Override
    public <T> T doAndReturnAsCurrentUser(Function<QuizUser, T> function) {
        return function.apply(getUserData());
    }

    @Override
    public void doAsCurrentUser(Consumer<QuizUser> consumer) {
        consumer.accept(getUserData());
    }

    private QuizUser getUserData() {
        return getAuthorizedUserData()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(CURRENT_USER_IS_NOT_DEFINED_MESSAGE));
    }

}
