package org.quizstorage.director.dao.repositories;

import org.junit.jupiter.api.Test;
import org.quizstorage.director.containers.MongodbContainerConfiguration;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.director.utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {MongodbContainerConfiguration.class})
@ActiveProfiles({"test", "integration-test"})
@Transactional
class QuizGameRepositoryTest {

    @Autowired
    private QuizGameRepository repository;

    @Test
    void shouldAddNewGame() {
        QuizGame newGame = TestData.NEW_QUIZ_GAME.toBuilder().build();
        repository.save(newGame);

        Optional<QuizGame> byId = repository.findById(newGame.getId());
        assertThat(byId)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(TestData.NEW_QUIZ_GAME);
    }

    @Test
    void shouldReturnFirstUnansweredQuestion() {
        String userId = "123";
        GameQuestion unansweredQuestion = TestData.GAME_QUESTION_BUILDER
                .number(3)
                .userAnswers(null)
                .answerDateTime(null)
                .build();

        TestData.GAME_QUESTION_BUILDER.userAnswers(Set.of("answers")).answerDateTime(Instant.now().truncatedTo(ChronoUnit.SECONDS));
        QuizGame game1 = TestData.EXISTED_QUIZ_GAME.toBuilder()
                .userId(userId)
                .questions(List.of(
                        TestData.GAME_QUESTION_BUILDER.number(1).build(),
                        TestData.GAME_QUESTION_BUILDER.number(2).build(),
                        TestData.GAME_QUESTION_BUILDER.number(3).build()
                ))
                .endDateTime(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
        QuizGame game2 = TestData.NEW_QUIZ_GAME.toBuilder()
                .userId(userId)
                .questions(List.of(
                        TestData.GAME_QUESTION_BUILDER.number(1).build(),
                        TestData.GAME_QUESTION_BUILDER.number(2).build(),
                        unansweredQuestion
                ))
                .endDateTime(null)
                .build();

        repository.saveAll(List.of(game1, game2));

        UnansweredGameQuestion expected = UnansweredGameQuestion.builder()
                .gameId(game2.getId())
                .sourceId(game2.getSourceId())
                .userId(game2.getUserId())
                .question(unansweredQuestion.getQuestion())
                .answers(unansweredQuestion.getAnswers())
                .difficulty(unansweredQuestion.getDifficulty())
                .correctAnswers(unansweredQuestion.getCorrectAnswers())
                .category(unansweredQuestion.getCategory())
                .number(unansweredQuestion.getNumber())
                .multiplyAnswers(unansweredQuestion.isMultiplyAnswers())
                .build();

        Optional<UnansweredGameQuestion> result = repository.findUnansweredQuestionForUser(userId);
        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void shouldNotReturnUnansweredQuestion() {
        repository.deleteAll();

        String userId = "1234";

        TestData.GAME_QUESTION_BUILDER.answerDateTime(Instant.now().truncatedTo(ChronoUnit.SECONDS)).userAnswers(Set.of("answers"));

        QuizGame game = TestData.EXISTED_QUIZ_GAME.toBuilder()
                .userId(userId)
                .endDateTime(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .questions(List.of(
                        TestData.GAME_QUESTION_BUILDER.number(1).question("1").build(),
                        TestData.GAME_QUESTION_BUILDER.number(2).question("2").build(),
                        TestData.GAME_QUESTION_BUILDER.number(3).question("3").build()
                ))
                .build();

        repository.save(game);

        Optional<UnansweredGameQuestion> result = repository.findUnansweredQuestionForUser(userId);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldCountUnfinishedGamesForUser() {
        String userId = "123";
        List<QuizGame> games = List.of(
                QuizGame.builder().userId(userId).endDateTime(Instant.now()).build(),
                QuizGame.builder().userId(userId).build(),
                QuizGame.builder().userId(userId).build()
        );
        repository.saveAll(games);
        long expected = games.stream().map(QuizGame::getEndDateTime).filter(Objects::isNull).count();

        long result = repository.countUnfinishedGamesForUser(userId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldReturnZeroWhenUserDoesNotHaveUnfinishedGames() {
        String userId = "any_user_without_opened_games";
        long result = repository.countUnfinishedGamesForUser(userId);

        assertThat(result).isEqualTo(0);
    }

}