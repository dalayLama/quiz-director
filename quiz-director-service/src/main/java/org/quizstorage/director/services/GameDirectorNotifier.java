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
import org.quizstorage.director.events.GameNotifier;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Primary
@Slf4j
@Validated
public class GameDirectorNotifier implements GameDirector {

    private final GameDirector delegate;

    private final GameNotifier notifier;

    @Override
    public GameInfo newGame(@NotNull @Valid QuestionSet questionSet) {
        GameInfo initializedGame = delegate.newGame(questionSet);
        notifier.notifyStartGame(initializedGame);
        return initializedGame;
    }

    @Override
    public Optional<GameQuestionDto> getCurrentQuestion() {
        return delegate.getCurrentQuestion();
    }

    @Override
    public AnswerResult acceptAnswers(@NotNull ObjectId gameId, @Positive Integer questionNumber, @NotEmpty Set<String> answers) {
        AnswerResult answerResult = delegate.acceptAnswers(gameId, questionNumber, answers);
        notifier.notifyAcceptAnswer(answerResult);
        if (answerResult.nextQuestion() == null) {
            notifier.notifyEndGame(answerResult.gameInfo());
        }
        return answerResult;
    }

    @Override
    public List<GameInfo> findFinishedGames() {
        return delegate.findFinishedGames();
    }

    @Override
    public Optional<GameResult> getGameResult(@NotNull ObjectId gameId) {
        return delegate.getGameResult(gameId);
    }

}
