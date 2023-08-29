package org.quizstorage.director.services;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.repositories.QuizGameRepository;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.director.exceptions.GameIsFinishedException;
import org.quizstorage.director.exceptions.GameQuestionAlreadyHasAnswer;
import org.quizstorage.director.utils.TestData;
import org.quizstorage.generator.dto.Question;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DaoQuizGameServiceTest {

    @Mock
    private QuizGameRepository repository;

    @InjectMocks
    private DaoQuizGameService service;

    @Test
    void shouldCreateNewGame() {
        String userId = "user-id";
        QuestionSet questionSet = TestData.QUESTION_SET;
        QuizGame expectedGame = expectedGame(userId, questionSet);
        given(repository.save(any(QuizGame.class))).willAnswer(returnsFirstArg());

        QuizGame quizGame = service.newGame(userId, questionSet);
        assertThat(quizGame).usingRecursiveComparison().isEqualTo(expectedGame);
    }

    @Test
    void shouldReturnUnansweredQuestion() {
        String userId = "1234";

        given(repository.findUnansweredQuestionForUser(userId))
                .willReturn(Optional.of(TestData.UNANSWERED_GAME_QUESTION));

        Optional<UnansweredGameQuestion> result = service.findUnansweredQuestion(userId);

        assertThat(result)
                .isPresent()
                .hasValue(TestData.UNANSWERED_GAME_QUESTION);
    }

    @Test
    void shouldSetAnswerForQuestion() {
        final Instant now = Instant.now();
        final String answer = "answers";
        int questionNumber = 2;
        final GameQuestion unansweredQuestion = TestData.GAME_QUESTION.toBuilder()
                .userAnswers(null)
                .answerDateTime(null)
                .number(questionNumber)
                .build();
        final QuizGame game = TestData.EXISTED_QUIZ_GAME.toBuilder()
                .questions(Stream.concat(
                        TestData.EXISTED_QUIZ_GAME.getQuestions().stream(),
                        Stream.of(unansweredQuestion)).toList()
                )
                .endDateTime(null)
                .build();

        given(repository.findById(game.getId())).willReturn(Optional.of(game));

        Pair<QuizGame, GameQuestion> result = service.setAnswers(game.getId(), questionNumber, Set.of(answer));

        SoftAssertions softAssertions = new SoftAssertions();
        QuizGame first = result.getFirst();
        GameQuestion second = result.getSecond();
        softAssertions.assertThat(first).isSameAs(game);
        softAssertions.assertThat(first.getEndDateTime()).isAfterOrEqualTo(now);
        softAssertions.assertThat(first.isFinished()).isTrue();
        softAssertions.assertThat(second.getNumber()).isEqualTo(questionNumber);
        softAssertions.assertThat(second.getUserAnswers()).isEqualTo(Set.of(answer));
        softAssertions.assertThat(second.getAnswerDateTime()).isAfterOrEqualTo(now);
        softAssertions.assertThat(second.isAnswered()).isTrue();
        softAssertions.assertAll();
    }

    @Test
    void shouldThrowGameIsFinishedException() {
        QuizGame game = TestData.EXISTED_QUIZ_GAME;
        String answer = "answers";

        given(repository.findById(game.getId())).willReturn(Optional.of(game));

        assertThatThrownBy(() -> service.setAnswers(game.getId(), 1, Set.of(answer)))
                .isInstanceOf(GameIsFinishedException.class);
    }

    @Test
    void shouldThrowGameQuestionAlreadyHasAnswer() {
        final String answer = "answers";
        int questionNumber = 2;
        final GameQuestion unansweredQuestion = TestData.GAME_QUESTION.toBuilder()
                .userAnswers(null)
                .answerDateTime(null)
                .number(questionNumber)
                .build();
        final QuizGame game = TestData.EXISTED_QUIZ_GAME.toBuilder()
                .questions(Stream.concat(
                        TestData.EXISTED_QUIZ_GAME.getQuestions().stream(),
                        Stream.of(unansweredQuestion)).toList()
                )
                .endDateTime(null)
                .build();

        given(repository.findById(game.getId())).willReturn(Optional.of(game));

        assertThatThrownBy(() -> service.setAnswers(game.getId(), 1, Set.of(answer)))
                .isInstanceOf(GameQuestionAlreadyHasAnswer.class);

    }

    @Test
    void shouldReturnFinishedGames() {
        String userId = "user-id";
        Sort expectedSort = Sort.by(Sort.Direction.DESC, "id");
        List<QuizGame> expected = List.of(TestData.EXISTED_QUIZ_GAME);

        given(repository.findFinishedGamesForUser(userId, expectedSort)).willReturn(expected);

        List<QuizGame> result = service.findFinishedGames(userId);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldReturnTrueWhenCountUnfinishedGamesGreaterThan0() {
        String userId = "userId";

        given(repository.countUnfinishedGamesForUser(userId)).willReturn(5L);

        boolean result = service.userHasUnfinishedGames(userId);

        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenCountUnfinishedGamesIs0() {
        String userId = "userId";

        given(repository.countUnfinishedGamesForUser(userId)).willReturn(0L);

        boolean result = service.userHasUnfinishedGames(userId);

        assertThat(result).isFalse();
    }

    private QuizGame expectedGame(String userId, QuestionSet questionSet) {
        List<GameQuestion> questions = IntStream
                .range(0, questionSet.questions().size())
                .mapToObj(index -> expectedGameQuestion(index + 1, questionSet.questions().get(index)))
                .toList();

        return QuizGame.builder()
                .userId(userId)
                .sourceId(questionSet.sourceId())
                .questions(questions)
                .build();
    }

    private GameQuestion expectedGameQuestion(int questionNumber, Question question) {
        return GameQuestion.builder()
                .number(questionNumber)
                .question(question.question())
                .answers(question.answers())
                .correctAnswers(question.correctAnswers())
                .category(question.category())
                .difficulty(question.difficulty())
                .multiplyAnswers(question.multiplyAnswers())
                .build();
    }


}