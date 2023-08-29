package org.quizstorage.director.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.components.GameAnalyzer;
import org.quizstorage.director.components.UserService;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.exceptions.GameResultGenerationException;
import org.quizstorage.director.exceptions.UserDoesNotOwnGameException;
import org.quizstorage.director.exceptions.UserHasUnfinishedGame;
import org.quizstorage.director.mappers.GameMapper;
import org.quizstorage.director.security.QuizUser;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class GameDirectorImpl implements GameDirector {

    private final UserService userService;

    private final QuizGameService quizGameService;

    private final GameMapper gameMapper;

    private final GameAnalyzer analyzer;

    @Override
    public GameInfo newGame(@NotNull @Valid QuestionSet questionSet) {
        QuizGame quizGame = userService.doAndReturnAsCurrentUser(user -> {
            checkExistedGames(user);
            return quizGameService.newGame(user.id(), questionSet);
        });
        log.info("A new game was initialized:\n{}", quizGame);
        return gameMapper.toGameInfo(quizGame);
    }

    @Override
    public Optional<GameQuestionDto> getCurrentQuestion() {
        return userService.doAndReturnAsCurrentUser(user -> quizGameService.findUnansweredQuestion(user.id()))
                .map(gameMapper::toGameQuestionDto);
    }

    @Override
    public AnswerResult acceptAnswers(@NotNull ObjectId gameId, @Positive Integer questionNumber, @NotEmpty Set<String> answers) {
        Pair<QuizGame, GameQuestion> pair = quizGameService.setAnswers(gameId, questionNumber, answers);
        log.info("The question of the game \"{}\" was answered:\n{}", pair.getFirst().getId(), pair.getSecond());
        QuizGame game = pair.getFirst();
        return findNextQuestion(game)
                .map(nextQuestion -> gameMapper.toAnswerResult(pair, nextQuestion))
                .orElseGet(() -> toAnswerResult(game, pair.getSecond().getNumber()));
    }

    @Override
    public List<GameInfo> findFinishedGames() {
        List<QuizGame> quizGames = userService.doAndReturnAsCurrentUser(user ->
                quizGameService.findFinishedGames(user.id()));
        return gameMapper.toGameInfoList(quizGames);
    }

    @Override
    public Optional<GameResult> getGameResult(@NotNull ObjectId gameId) {
        return userService.doAndReturnAsCurrentUser(user ->
                quizGameService.findById(gameId).map(game -> analyze(user, game)));
    }

    private GameResult analyze(QuizUser quizUser, QuizGame game) {
        checkUserOwnsGame(quizUser, game);
        if (!game.isFinished()) {
            String message = "Game \"%s\" isn't finished yet".formatted(game.getId());
            throw new GameResultGenerationException(message, HttpStatus.BAD_REQUEST);
        }
        return analyzer.calculateResult(game);
    }

    private Optional<GameQuestion> findNextQuestion(QuizGame game) {
        if (game.isFinished()) {
            return Optional.empty();
        }
        return IntStream
                .range(0, game.getQuestions().size())
                .filter(index -> !game.getQuestions().get(index).isAnswered())
                .mapToObj(index -> game.getQuestions().get(index))
                .findFirst();
    }

    private void checkUserOwnsGame(QuizUser quizUser, QuizGame game) {
        if (!Objects.equals(quizUser.id(), game.getUserId())) {
            throw new UserDoesNotOwnGameException(game.getId());
        }
    }

    private void checkExistedGames(QuizUser user) {
        if (quizGameService.userHasUnfinishedGames(user.id())) {
            throw new UserHasUnfinishedGame();
        }
    }

    private AnswerResult toAnswerResult(QuizGame game, int answeredQuestionNumber) {
        GameInfo gameInfo = gameMapper.toGameInfo(game);
        return new AnswerResult(gameInfo, answeredQuestionNumber, null);
    }

}
