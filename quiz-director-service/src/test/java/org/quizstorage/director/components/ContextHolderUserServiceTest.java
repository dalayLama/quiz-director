package org.quizstorage.director.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.security.QuizUserAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@ExtendWith(MockitoExtension.class)
class ContextHolderUserServiceTest {

    private static final QuizUser USER_DATA = new QuizUser("id", "name", Set.of("role"));

    private static final QuizUserAuthenticationToken TOKEN = new QuizUserAuthenticationToken(
            USER_DATA, Set.of(new SimpleGrantedAuthority("role")));

    @Mock
    private SecurityContext securityContext;

    @Mock
    private SomeService someService;

    @InjectMocks
    private ContextHolderUserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldReturnUserData() {
        given(securityContext.getAuthentication()).willReturn(TOKEN);

        Optional<QuizUser> authorizedUserData = userService.getAuthorizedUserData();
        assertThat(authorizedUserData)
                .isPresent()
                .hasValue(USER_DATA);
    }

    @Test
    void shouldNotReturnUseData() {
        given(securityContext.getAuthentication()).willReturn(null);

        Optional<QuizUser> authorizedUserData = userService.getAuthorizedUserData();
        assertThat(authorizedUserData).isEmpty();
    }

    @Test
    void shouldDoActionAsCurrentUser() {
        given(securityContext.getAuthentication()).willReturn(TOKEN);

        userService.doAsCurrentUser(userData -> someService.doSomething(userData));

        then(someService).should(only()).doSomething(USER_DATA);
    }

    @Test
    void shouldDoActionAndReturnAsCurrentUser() {
        String expectedValue = "value";
        given(securityContext.getAuthentication()).willReturn(TOKEN);
        given(someService.doSomethingAndReturn(USER_DATA)).willReturn(expectedValue);

        String result = userService.doAndReturnAsCurrentUser(userData -> someService.doSomethingAndReturn(userData));
        assertThat(result).isEqualTo(expectedValue);
        then(someService).should(only()).doSomethingAndReturn(USER_DATA);
    }

    @Test
    void shouldThrowExceptionIfUserIsNotDefined() {
        given(securityContext.getAuthentication()).willReturn(null);

        assertThatThrownBy(() -> userService.doAsCurrentUser(userData -> someService.doSomething(userData)))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    private interface SomeService {

        void doSomething(QuizUser quizUser);

        String doSomethingAndReturn(QuizUser quizUser);

    }

}