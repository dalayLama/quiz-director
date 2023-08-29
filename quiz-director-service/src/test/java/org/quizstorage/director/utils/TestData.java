package org.quizstorage.director.utils;

import lombok.experimental.UtilityClass;
import org.bson.types.ObjectId;
import org.quizstoradge.director.dto.*;
import org.quizstorage.director.dao.entities.UserWebSocketTokenData;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.generator.dto.Difficulty;
import org.quizstorage.generator.dto.Question;
import org.quizstorage.generator.dto.QuestionSet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class TestData {

    public static final QuizUser QUIZ_USER = new QuizUser("123", "name", Set.of("role"));

    public static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static ZoneOffset getZoneOffset(LocalDateTime localDateTime) {
        return ZONE_ID.getRules().getOffset(localDateTime);
    }

    public static Instant createInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(getZoneOffset(localDateTime));
    }

    public static GameInfo toGameInfo(QuizGame quizGame) {
        return new GameInfo(
                quizGame.getId().toString(),
                quizGame.getUserId(),
                quizGame.getSourceId(),
                quizGame.getId().getDate().toInstant(),
                quizGame.getEndDateTime()
        );
    }

    public static GameInfo toGameInfo(UnansweredGameQuestion gameQuestion) {
        return GameInfo.builder()
                .id(gameQuestion.getGameId().toString())
                .userId(gameQuestion.getUserId())
                .sourceId(gameQuestion.getSourceId())
                .start(gameQuestion.getGameId().getDate().toInstant())
                .build();
    }

    public static final Question QUESTION = Question.builder()
            .question("question")
            .answers(Set.of("answer1", "answer2"))
            .correctAnswers(Set.of("answer"))
            .multiplyAnswers(false)
            .category("category")
            .difficulty(Difficulty.EASY)
            .build();

    public static final GameQuestion.GameQuestionBuilder GAME_QUESTION_BUILDER = GameQuestion.builder()
            .number(1)
            .question(QUESTION.question())
            .answers(QUESTION.answers())
            .correctAnswers(QUESTION.correctAnswers())
            .multiplyAnswers(QUESTION.multiplyAnswers())
            .category(QUESTION.category())
            .difficulty(QUESTION.difficulty())
            .userAnswers(Set.of("answers"))
            .answerDateTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));

    public static final QuestionSet QUESTION_SET = new QuestionSet(
            "source-id",
            List.of(QUESTION)
    );

    public static final QuizGame EXISTED_QUIZ_GAME = QuizGame.builder()
            .id(new ObjectId("64c4cc39ae0ae6347ef6c8b2"))
            .userId(QUIZ_USER.id())
            .sourceId("source-id")
            .endDateTime(createInstant(LocalDateTime.of(2023, 8, 1, 0, 0, 0)))
            .questions(List.of(GAME_QUESTION_BUILDER.build()))
            .build();

    public static final QuizGame NEW_QUIZ_GAME = QuizGame.builder()
            .userId("user-id")
            .sourceId("source-id")
            .endDateTime(createInstant(LocalDateTime.of(2023, 8, 1, 0, 0, 0)))
            .questions(List.of(GAME_QUESTION_BUILDER.build()))
            .build();

    public static final GameQuestion GAME_QUESTION = GAME_QUESTION_BUILDER.build();

    public static final UnansweredGameQuestion UNANSWERED_GAME_QUESTION = UnansweredGameQuestion.builder()
            .gameId(EXISTED_QUIZ_GAME.getId())
            .userId(EXISTED_QUIZ_GAME.getUserId())
            .sourceId(EXISTED_QUIZ_GAME.getSourceId())
            .question(GAME_QUESTION.getQuestion())
            .answers(GAME_QUESTION.getAnswers())
            .correctAnswers(GAME_QUESTION.getCorrectAnswers())
            .multiplyAnswers(GAME_QUESTION.isMultiplyAnswers())
            .category(GAME_QUESTION.getCategory())
            .difficulty(GAME_QUESTION.getDifficulty())
            .build();

    public static final GameInfo GAME_INFO = toGameInfo(EXISTED_QUIZ_GAME);

    public static final GameQuestionDto GAME_QUESTION_DTO = GameQuestionDto.builder()
            .gameInfo(toGameInfo(EXISTED_QUIZ_GAME))
            .question(GAME_QUESTION.getQuestion())
            .answers(GAME_QUESTION.getAnswers())
            .multiplyAnswers(GAME_QUESTION.isMultiplyAnswers())
            .number(GAME_QUESTION.getNumber())
            .category("category")
            .build();

    public static final AnswerResult ANSWER_RESULT = new AnswerResult(
            TestData.GAME_QUESTION_DTO.gameInfo(),
            TestData.GAME_QUESTION_DTO.number(),
            TestData.GAME_QUESTION_DTO.toBuilder()
                    .number(TestData.GAME_QUESTION_DTO.number() + 1)
                    .build()
    );

    public static final GameResult GAME_RESULT = new GameResult( 1, 1);

    public static final UserWebSocketTokenData USER_WEB_SOCKET_TOKEN_DATA = UserWebSocketTokenData.builder()
            .userId(QUIZ_USER.id())
            .tokenId(UUID.fromString("bc68fb0c-e898-4f72-b198-0e87c914a6dd"))
            .name(QUIZ_USER.name())
            .roles(QUIZ_USER.roles())
            .build();

    public static final QuestionResult CORRECT_QUESTION_RESULT = new QuestionResult(
            GAME_QUESTION.getNumber(),
            GAME_QUESTION.getQuestion(),
            GAME_QUESTION.getCategory(),
            GAME_QUESTION.getUserAnswers(),
            true
    );

}
