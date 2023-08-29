package org.quizstoradge.director.dto;

import java.util.Set;

public record QuestionResult(
        int number,
        String question,
        String category,
        Set<String> userAnswers,
        boolean correct
) {
}
