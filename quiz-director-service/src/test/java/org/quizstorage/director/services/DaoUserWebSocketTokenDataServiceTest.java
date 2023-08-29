package org.quizstorage.director.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.dao.repositories.UserWebSocketTokenDataRepository;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.utils.TestData;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;

@ExtendWith(MockitoExtension.class)
class DaoUserWebSocketTokenDataServiceTest {

    @Mock
    private UserWebSocketTokenDataRepository repository;

    @InjectMocks
    private DaoUserWebSocketTokenDataService service;

    @Test
    void shouldFindAndRemoveTokenByTokenId() {
        // Given
        UUID tokenId = UUID.randomUUID();
        UserWebSocketTokenData tokenData = new UserWebSocketTokenData();
        given(repository.findByTokenId(tokenId)).willReturn(Optional.of(tokenData));

        // When
        Optional<UserWebSocketTokenData> result = service.findAndRemoveByTokenId(tokenId);

        // Then
        assertThat(result).containsSame(tokenData);
        then(repository).should().delete(tokenData);
    }

    @Test
    void shouldReturnEmptyIfTokenNotFoundWhenFindingAndRemoving() {
        // Given
        UUID tokenId = UUID.randomUUID();
        given(repository.findByTokenId(tokenId)).willReturn(Optional.empty());

        // When
        Optional<UserWebSocketTokenData> result = service.findAndRemoveByTokenId(tokenId);

        // Then
        assertThat(result).isEmpty();
        then(repository).should(never()).delete(any());
    }

    @Test
    void shouldGenerateTokenForUser() {
        // Given
        QuizUser user = TestData.QUIZ_USER;
        given(repository.save(any(UserWebSocketTokenData.class))).will(returnsFirstArg());
        UserWebSocketTokenData expectedTokenData = UserWebSocketTokenData.builder()
                .userId(user.id())
                .roles(user.roles())
                .name(user.name())
                .build();

        // When
        UserWebSocketTokenData result = service.generateToken(user);

        // Then
        assertThat(result)
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("tokenId")
                .isEqualTo(expectedTokenData);
        then(repository).should(only()).save(result);
    }

}
