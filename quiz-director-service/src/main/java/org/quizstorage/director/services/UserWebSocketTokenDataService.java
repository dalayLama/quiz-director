package org.quizstorage.director.services;

import jakarta.validation.constraints.NotNull;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserWebSocketTokenDataService {

    Optional<UserWebSocketTokenData> findAndRemoveByTokenId(@NotNull UUID tokenId);

    @Transactional
    UserWebSocketTokenData generateToken(QuizUser user);
}
