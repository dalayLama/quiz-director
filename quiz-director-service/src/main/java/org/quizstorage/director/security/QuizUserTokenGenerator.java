package org.quizstorage.director.security;

import java.util.Optional;
import java.util.function.Function;

public interface QuizUserTokenGenerator {

    Optional<QuizUserAuthenticationToken> create(Function<String, String> userFieldsProvider);

    Optional<QuizUserAuthenticationToken> createByUserId(String userId);

    Optional<WebSocketAuthenticationToken> createByWebSocketTokenId(Function<String, String> tokenIdProvider);
}
