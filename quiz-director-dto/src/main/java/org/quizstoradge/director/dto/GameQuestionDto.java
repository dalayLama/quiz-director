package org.quizstoradge.director.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.Set;

@Builder(toBuilder = true)
public record GameQuestionDto(
        GameInfo gameInfo,
        int number,
        String question,
        Set<String> answers,
        String category,
        boolean multiplyAnswers
) implements Serializable {
}
