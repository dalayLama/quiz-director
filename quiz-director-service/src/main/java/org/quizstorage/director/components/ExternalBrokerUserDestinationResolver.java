package org.quizstorage.director.components;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quizstorage.director.security.QuizUserAuthenticationToken;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.messaging.simp.user.UserDestinationResult;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExternalBrokerUserDestinationResolver implements UserDestinationResolver {

    private static final Pattern USER_DEST_PREFIXING_PATTERN =
            Pattern.compile("/user/(?<name>.+?)/(?<dest>.+?)");

    private static final Pattern USER_AUTHENTICATED_PATTERN =
            Pattern.compile("/user/(?<dest>.+?)");

    private static final String SPECIFIC_USER_EXCHANGE_ROUTE = "/exchange/amq.direct";

    @Override
    public UserDestinationResult resolveDestination(Message<?> message) {
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, SimpMessageHeaderAccessor.class);
        if (accessor == null || accessor.getMessageType() == null) {
            return null;
        }

        final String destination = accessor.getDestination();
        final String authUser = getUserId(accessor).orElse(null);
        log.trace("Resolving user destination {} for authUser={}, messageType={}",
                destination, authUser, accessor.getMessageType());

        UserDestinationResult result = switch (accessor.getMessageType()) {
            case SUBSCRIBE, UNSUBSCRIBE -> subscribing(authUser, destination);
            case MESSAGE -> messaging(destination);
            default -> null;
        };
        if (result == null) {
            log.trace("Destination {} is not user-based", destination);
        }
        return result;
    }

    private UserDestinationResult subscribing(String authUser, String destination) {
        if (StringUtils.isBlank(authUser)) {
            return null;
        }
        final Matcher authMatcher = USER_AUTHENTICATED_PATTERN.matcher(destination);
        if (authMatcher.matches()) {
            String result = specificUserQueue(authUser, authMatcher.group("dest"));
            return new UserDestinationResult(destination, Collections.singleton(result), result, authUser);
        }
        return null;
    }

    private UserDestinationResult messaging(String destination) {
        final Matcher prefixMatcher = USER_DEST_PREFIXING_PATTERN.matcher(destination);
        if (prefixMatcher.matches()) {
            String user = prefixMatcher.group("name");
            String userDestination = prefixMatcher.group("dest");
            String result = specificUserQueue(user, userDestination);
            return new UserDestinationResult(destination, Collections.singleton(result), result, user);
        }
        return null;
    }

    private String specificUserQueue(String username, String destination) {
        return "%s/users.%s.%s".formatted(
                SPECIFIC_USER_EXCHANGE_ROUTE,
                username,
                destination.replace('/', '.')
        );
    }

    private Optional<String> getUserId(SimpMessageHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            return Optional.empty();
        }
        if (principal instanceof QuizUserAuthenticationToken) {
            String id = ((QuizUserAuthenticationToken) principal).getUserData().id();
            return Optional.of(id);
        } else {
            return Optional.ofNullable(principal.getName());
        }
    }

}