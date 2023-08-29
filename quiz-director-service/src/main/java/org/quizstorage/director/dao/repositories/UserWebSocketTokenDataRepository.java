package org.quizstorage.director.dao.repositories;

import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserWebSocketTokenDataRepository extends MongoRepository<UserWebSocketTokenData, String> {

    @Query("{ tokenId: {$eq: ?0} }")
    Optional<UserWebSocketTokenData> findByTokenId(UUID token);

}
