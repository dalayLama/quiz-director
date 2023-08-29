package org.quizstoradge.director.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record QuizGameAnalysis(
        GameInfo gameInfo,
        GameResult gameResult,
        List<QuestionResult> questionResults
) {
}
