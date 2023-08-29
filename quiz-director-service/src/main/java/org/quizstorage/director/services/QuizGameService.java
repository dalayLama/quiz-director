package org.quizstorage.director.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.bson.types.ObjectId;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuizGameService {

    boolean userHasUnfinishedGames(@NotBlank String userId);

    QuizGame newGame(@NotBlank String userId, @NotNull @Valid QuestionSet questionSet);

    Optional<UnansweredGameQuestion> findUnansweredQuestion(@NotNull String userId);

    Pair<QuizGame, GameQuestion> setAnswers(@NotNull ObjectId gameId,
                                            @Positive int questionNumber,
                                            @NotEmpty Set<String> answer);

    List<QuizGame> findFinishedGames(@NotBlank String userId);

    Optional<QuizGame> findById(@NotNull ObjectId gameId);
}
