package org.quizstorage.director.mappers;

import org.junit.jupiter.api.Test;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.director.utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {GameMapperImpl.class, DateTimeMapperImpl.class})
class GameMapperTest {

    @Autowired
    private GameMapper mapper;

    @Test
    void shouldConvertNewGameCorrectly() {
        QuizGame game = TestData.EXISTED_QUIZ_GAME;
        GameInfo expected = TestData.toGameInfo(game);

        GameInfo result = mapper.toGameInfo(game);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldConvertGameQuestionDtoFromUnansweredQuestionCorrectly() {
        UnansweredGameQuestion unansweredGameQuestion = TestData.UNANSWERED_GAME_QUESTION;
        GameQuestionDto expected = new GameQuestionDto(
                TestData.toGameInfo(unansweredGameQuestion),
                unansweredGameQuestion.getNumber(),
                unansweredGameQuestion.getQuestion(),
                unansweredGameQuestion.getAnswers(),
                unansweredGameQuestion.getCategory(),
                unansweredGameQuestion.isMultiplyAnswers());

        GameQuestionDto result = mapper.toGameQuestionDto(unansweredGameQuestion);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldConvertGameQuestionDtoFromGameQuestionCorrectly() {
        GameQuestion gameQuestion = TestData.GAME_QUESTION;
        QuizGame game = TestData.EXISTED_QUIZ_GAME;
        GameQuestionDto expected = new GameQuestionDto(
                TestData.toGameInfo(game),
                gameQuestion.getNumber(),
                gameQuestion.getQuestion(),
                gameQuestion.getAnswers(),
                gameQuestion.getCategory(),
                gameQuestion.isMultiplyAnswers());

        GameQuestionDto result = mapper.toGameQuestionDto(game, gameQuestion);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldConvertToFinishedGamesListCorrectly() {
        QuizGame game = TestData.EXISTED_QUIZ_GAME;
        List<GameInfo> expected = List.of(TestData.toGameInfo(game));

        List<GameInfo> result = mapper.toGameInfoList(List.of(game));

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    void shouldConvertToAnswerResult() {
        GameInfo gameInfo = TestData.toGameInfo(TestData.EXISTED_QUIZ_GAME);
        GameQuestion nextQuestion = TestData.GAME_QUESTION.toBuilder()
                .number(TestData.GAME_QUESTION.getNumber() + 1)
                .build();
        AnswerResult expected = new AnswerResult(
                gameInfo,
                TestData.GAME_QUESTION.getNumber(),
                GameQuestionDto.builder()
                        .gameInfo(gameInfo)
                        .question(nextQuestion.getQuestion())
                        .answers(nextQuestion.getAnswers())
                        .number(nextQuestion.getNumber())
                        .category(nextQuestion.getCategory())
                        .multiplyAnswers(nextQuestion.isMultiplyAnswers())
                        .build()
        );

        AnswerResult result = mapper.toAnswerResult(
                Pair.of(TestData.EXISTED_QUIZ_GAME, TestData.GAME_QUESTION), nextQuestion);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}