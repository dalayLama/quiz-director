package org.quizstorage.director.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.components.GameAnalyzerImpl;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.mappers.GameMapper;
import org.quizstorage.director.utils.TestData;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GameAnalyzerImplTest {

    private static final QuizGame QUIZ_GAME = TestData.EXISTED_QUIZ_GAME.toBuilder()
            .questions(List.of(
                    GameQuestion.builder()
                            .number(1)
                            .question("q1")
                            .category("category")
                            .correctAnswers(Set.of("answer1"))
                            .userAnswers(Set.of("answer1")).build(), // correct
                    GameQuestion.builder()
                            .number(2)
                            .question("q2")
                            .category("category")
                            .correctAnswers(Set.of("answer1", "answer2"))
                            .userAnswers(Set.of("answer1", "answer2")).build(), // correct
                    GameQuestion.builder()
                            .number(3)
                            .question("q3")
                            .category("category")
                            .correctAnswers(Set.of("answer1"))
                            .userAnswers(Set.of("answer2")).build(), // not correct
                    GameQuestion.builder()
                            .number(4)
                            .question("q4")
                            .category("category")
                            .correctAnswers(Set.of("answer1", "answer2"))
                            .userAnswers(Set.of("answer1", "answer3")).build(), // not correct
                    GameQuestion.builder()
                            .number(5)
                            .question("q5")
                            .category("category")
                            .correctAnswers(Set.of("answer1", "answer2"))
                            .userAnswers(Set.of("answer1")).build() // not correct
            ))
            .build();

    private static final GameResult EXPECTED_GAME_RESULT = new GameResult( 5, 2);

    @Mock
    private GameMapper mapper;

    @InjectMocks
    private GameAnalyzerImpl analyzer;

    @Test
    void shouldCalculateResultCorrectly() {
        GameResult result = analyzer.calculateResult(QUIZ_GAME);

        assertThat(result).usingRecursiveComparison().isEqualTo(EXPECTED_GAME_RESULT);
    }


}