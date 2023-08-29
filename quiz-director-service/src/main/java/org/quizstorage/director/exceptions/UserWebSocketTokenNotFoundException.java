package org.quizstorage.director.exceptions;

import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

public class UserWebSocketTokenNotFoundException extends AuthenticationException {

    private static final String MESSAGE_TEMPLATE = "User data hasn't been found by web socket token id \"%s\"";

    public UserWebSocketTokenNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserWebSocketTokenNotFoundException(String msg) {
        super(msg);
    }

    public UserWebSocketTokenNotFoundException(UUID tokenId) {
        this(MESSAGE_TEMPLATE.formatted(tokenId));
    }

}
