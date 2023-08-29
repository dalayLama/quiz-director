package org.quizstorage.director.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.bson.types.ObjectId;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.generator.dto.QuestionSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GameDirector {

    GameInfo newGame(@NotNull @Valid QuestionSet questionSet);

    Optional<GameQuestionDto> getCurrentQuestion();

    AnswerResult acceptAnswers(@NotNull ObjectId gameId, @Positive Integer questionNumber, @NotEmpty Set<String> answers);

    List<GameInfo> findFinishedGames();

    Optional<GameResult> getGameResult(@NotNull ObjectId gameId);

}
