package org.quizstorage.director.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.quizstorage.director.configurations.properties.AuthenticationHeadersProperties;
import org.quizstorage.director.utils.ConvertUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuizUserHeaderTokenGenerator implements QuizUserTokenGenerator {

    private final AuthenticationHeadersProperties properties;

    @Override
    public Optional<QuizUserAuthenticationToken> create(Function<String, String> headersFieldsProvider) {
        return extractUserDataHeaders(headersFieldsProvider);
    }

    @Override
    public Optional<QuizUserAuthenticationToken> createByUserId(String userId) {
        return Optional.ofNullable(userId)
                .filter(StringUtils::isNotBlank)
                .map(id -> new QuizUserAuthenticationToken(
                        new QuizUser(id, null, Collections.emptySet()), Collections.emptyList())
                );
    }

    @Override
    public Optional<WebSocketAuthenticationToken> createByWebSocketTokenId(
            Function<String, String> tokenIdProvider) {
        String headerName = properties.getWebSocketTokenIdHeaderName();
        return Optional.ofNullable(tokenIdProvider.apply(headerName))
                .map(headerValue -> ConvertUtils.convert(headerValue, UUID::fromString))
                .map(WebSocketAuthenticationToken::new);
    }

    private Optional<QuizUserAuthenticationToken> extractUserDataHeaders(Function<String, String> headersProvider) {
        String userId = headersProvider.apply(properties.getIdHeaderName());
        return Optional.ofNullable(userId)
                .filter(StringUtils::isNotBlank)
                .map(id -> createToken(id, headersProvider));
    }

    private QuizUserAuthenticationToken createToken(String userId, Function<String, String> headerProvider) {
        QuizUser quizUser = createUserData(userId, headerProvider);
        Set<SimpleGrantedAuthority> authorities = quizUser.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new QuizUserAuthenticationToken(quizUser, authorities);
    }

    private QuizUser createUserData(String userId, Function<String, String> headerProvider) {
        String username = headerProvider.apply(properties.getNameHeaderName());
        Set<String> roles = extractRoles(headerProvider.apply(properties.getRolesHeaderName()));
        return new QuizUser(userId, username, roles);
    }

    private Set<String> extractRoles(String rolesString) {
        return Optional.ofNullable(rolesString)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(roles -> roles.split(Pattern.quote(",")))
                .stream()
                .flatMap(Arrays::stream)
                .map(String::trim)
                .collect(Collectors.toSet());
    }

}
