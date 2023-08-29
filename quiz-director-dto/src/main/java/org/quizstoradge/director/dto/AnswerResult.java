package org.quizstoradge.director.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record AnswerResult(
        GameInfo gameInfo,
        int questionNumber,
        GameQuestionDto nextQuestion
) {
}
