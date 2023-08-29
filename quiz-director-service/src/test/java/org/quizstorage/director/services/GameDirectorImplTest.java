package org.quizstorage.director.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.components.GameAnalyzer;
import org.quizstorage.director.components.UserService;
import org.quizstorage.director.exceptions.UserHasUnfinishedGame;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.director.exceptions.GameResultGenerationException;
import org.quizstorage.director.exceptions.UserDoesNotOwnGameException;
import org.quizstorage.director.mappers.GameMapper;
import org.quizstorage.director.utils.TestData;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class GameDirectorImplTest {

    @Mock
    private UserService userService;

    @Mock
    private QuizGameService gameService;

    @Mock
    private GameMapper gameMapper;

    @Mock
    private GameAnalyzer analyzer;

    @InjectMocks
    private GameDirectorImpl director;

    @Test
    void shouldCreateNewGame() {
        QuizGame game = QuizGame.builder().build();
        GameInfo initializedGame = GameInfo.builder().build();

        willAnswer(invocation -> {
            Function<QuizUser, QuizGame> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));

        given(gameService.userHasUnfinishedGames(TestData.QUIZ_USER.id())).willReturn(false);
        given(gameService.newGame(TestData.QUIZ_USER.id(), TestData.QUESTION_SET)).willReturn(game);
        given(gameMapper.toGameInfo(game)).willReturn(initializedGame);

        GameInfo result = director.newGame(TestData.QUESTION_SET);

        assertThat(result).usingRecursiveComparison().isEqualTo(initializedGame);
        then(gameService).should().userHasUnfinishedGames(TestData.QUIZ_USER.id());
        then(gameService).should().newGame(TestData.QUIZ_USER.id(), TestData.QUESTION_SET);
        then(gameMapper).should(only()).toGameInfo(game);
    }

    @Test
    void shouldReturnCurrentQuestion() {
        willAnswer(invocation -> {
            Function<QuizUser, UnansweredGameQuestion> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));

        given(gameService.findUnansweredQuestion(TestData.QUIZ_USER.id()))
                .willReturn(Optional.of(TestData.UNANSWERED_GAME_QUESTION));
        given(gameMapper.toGameQuestionDto(TestData.UNANSWERED_GAME_QUESTION))
                .willReturn(TestData.GAME_QUESTION_DTO);

        Optional<GameQuestionDto> result = director.getCurrentQuestion();

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(TestData.GAME_QUESTION_DTO);
    }

    @Test
    void shouldAcceptAnswer() {
        final String answer = "answers";
        int questionNumber = 1;
        final GameQuestion unansweredQuestion = TestData.GAME_QUESTION.toBuilder()
                .number(2)
                .userAnswers(null)
                .answerDateTime(null)
                .build();
        final QuizGame game = TestData.EXISTED_QUIZ_GAME.toBuilder()
                .questions(Stream.concat(
                        TestData.EXISTED_QUIZ_GAME.getQuestions().stream(),
                        Stream.of(unansweredQuestion)).toList()
                )
                .endDateTime(null)
                .build();
        Pair<QuizGame, GameQuestion> serviceResult = Pair.of(game, TestData.GAME_QUESTION);
        AnswerResult expectedResult = AnswerResult.builder().build();

        given(gameService.setAnswers(game.getId(), 1, Set.of(answer))).willReturn(serviceResult);
        given(gameMapper.toAnswerResult(serviceResult, unansweredQuestion)).willReturn(expectedResult);

        AnswerResult answerResult = director.acceptAnswers(game.getId(), questionNumber, Set.of(answer));

        assertThat(answerResult).isSameAs(expectedResult);
    }

    @Test
    void shouldAcceptAnswerAndNotReturnNextQuestion() {
        final String answer = "answers";
        QuizGame game = TestData.EXISTED_QUIZ_GAME;

        given(gameService.setAnswers(game.getId(), 1, Set.of(answer)))
                .willReturn(Pair.of(game, TestData.GAME_QUESTION));

        AnswerResult answerResult = director.acceptAnswers(game.getId(), 1, Set.of(answer));

        assertThat(answerResult.nextQuestion()).isNull();
    }

    @Test
    void shouldReturnFinishedGames() {
        List<QuizGame> serviceResult = List.of(TestData.EXISTED_QUIZ_GAME);
        QuizUser quizUser = TestData.QUIZ_USER;
        List<GameInfo> expected = List.of(TestData.GAME_INFO);

        willAnswer(invocation -> {
            Function<QuizUser, List<QuizGame>> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));
        given(gameService.findFinishedGames(quizUser.id())).willReturn(serviceResult);
        given(gameMapper.toGameInfoList(serviceResult)).willReturn(expected);

        List<GameInfo> result = director.findFinishedGames();

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldReturnGameResult() {
        QuizGame game = TestData.EXISTED_QUIZ_GAME;
        GameResult expected = TestData.GAME_RESULT;

        willAnswer(invocation -> {
            Function<QuizUser, Optional<QuizGame>> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));
        given(gameService.findById(game.getId())).willReturn(Optional.of(game));
        given(analyzer.calculateResult(game)).willReturn(expected);

        Optional<GameResult> result = director.getGameResult(game.getId());

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void shouldThrowUserDoesNotOwnGameException() {
        QuizUser quizUser = TestData.QUIZ_USER.toBuilder().id("different-id").build();
        QuizGame game = TestData.EXISTED_QUIZ_GAME;

        willAnswer(invocation -> {
            Function<QuizUser, Optional<QuizGame>> function = invocation.getArgument(0);
            return function.apply(quizUser);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));
        given(gameService.findById(game.getId())).willReturn(Optional.of(game));

        assertThatThrownBy(() -> director.getGameResult(game.getId()))
                .isInstanceOf(UserDoesNotOwnGameException.class);
    }

    @Test
    void shouldThrowGameResultGenerationException() {
        QuizGame game = TestData.EXISTED_QUIZ_GAME.toBuilder().endDateTime(null).build();

        willAnswer(invocation -> {
            Function<QuizUser, Optional<QuizGame>> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));
        given(gameService.findById(game.getId())).willReturn(Optional.of(game));

        assertThatThrownBy(() -> director.getGameResult(game.getId()))
                .isInstanceOf(GameResultGenerationException.class);
    }

    @Test
    void shouldThrowUserHasUnfinishedGame() {
        QuestionSet anyQuestionSet = new QuestionSet(null, null);

        given(gameService.userHasUnfinishedGames(TestData.QUIZ_USER.id())).willReturn(true);
        willAnswer(invocation -> {
            Function<QuizUser, QuizGame> function = invocation.getArgument(0);
            return function.apply(TestData.QUIZ_USER);
        }).given(userService).doAndReturnAsCurrentUser(any(Function.class));

        assertThatThrownBy(() -> director.newGame(anyQuestionSet)).isInstanceOf(UserHasUnfinishedGame.class);
    }


}