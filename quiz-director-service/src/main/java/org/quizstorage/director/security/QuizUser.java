package org.quizstorage.director.security;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record QuizUser (
        String id,
        String name,
        Set<String> roles
){}
