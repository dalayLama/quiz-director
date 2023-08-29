package org.quizstorage.director.dao.repositories;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quizstorage.director.containers.MongodbContainerConfiguration;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {MongodbContainerConfiguration.class})
@ActiveProfiles({"test", "integration-test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserWebSocketTokenDataRepositoryTest {

    @Autowired
    private UserWebSocketTokenDataRepository tokenDataRepository;

    @Test
    @Order(1)
    void shouldGenerateNewToken() {
        UserWebSocketTokenData userWebSocketTokenData = TestData.USER_WEB_SOCKET_TOKEN_DATA;
        tokenDataRepository.save(userWebSocketTokenData);

        assertThat(tokenDataRepository.existsById(userWebSocketTokenData.getUserId())).isTrue();
    }

    @Test
    @Order(2)
    void shouldReturnExistedToken() {
        Optional<UserWebSocketTokenData> result = tokenDataRepository
                .findByTokenId(TestData.USER_WEB_SOCKET_TOKEN_DATA.getTokenId());

        assertThat(result)
                .get()
                .usingRecursiveComparison()
                .isEqualTo(TestData.USER_WEB_SOCKET_TOKEN_DATA);
    }

    @Test
    void shouldReturnEmptyIfTokenDoesNotExist() {
        UUID nonexistentToken = UUID.randomUUID();

        Optional<UserWebSocketTokenData> result = tokenDataRepository.findByTokenId(nonexistentToken);
        assertThat(result).isEmpty();
    }
}