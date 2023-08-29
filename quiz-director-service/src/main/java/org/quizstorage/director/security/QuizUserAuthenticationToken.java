package org.quizstorage.director.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuizUserAuthenticationToken implements Authentication {

    private final Object credentials;

    private final QuizUser quizUser;

    private final List<GrantedAuthority> authorities;

    private boolean authenticated;

    public QuizUserAuthenticationToken(QuizUser quizUser,
                                       Collection<? extends GrantedAuthority> authorities) {
        this(quizUser, quizUser, authorities);
    }

    public QuizUserAuthenticationToken(Object credentials,
                                       QuizUser quizUser,
                                       Collection<? extends GrantedAuthority> authorities) {
        this.credentials = credentials;
        this.quizUser = quizUser;
        this.authorities = new ArrayList<>(authorities);
        this.authenticated = this.quizUser != null && this.quizUser.id() != null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return getName();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return quizUser.name();
    }

    public QuizUser getUserData() {
        return quizUser;
    }

}
