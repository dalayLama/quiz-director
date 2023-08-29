package org.quizstorage.director.services;

import lombok.RequiredArgsConstructor;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.dao.repositories.UserWebSocketTokenDataRepository;
import org.quizstorage.director.security.QuizUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DaoUserWebSocketTokenDataService implements UserWebSocketTokenDataService {

    private final UserWebSocketTokenDataRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UserWebSocketTokenData> findAndRemoveByTokenId(UUID tokenId) {
        return repository.findByTokenId(tokenId)
                .map(tokenData -> {
                    repository.delete(tokenData);
                    return tokenData;
                });
    }

    @Override
    @Transactional
    public UserWebSocketTokenData generateToken(QuizUser user) {
        UserWebSocketTokenData tokenData = new UserWebSocketTokenData(user, UUID.randomUUID());
        return repository.save(tokenData);
    }

}
