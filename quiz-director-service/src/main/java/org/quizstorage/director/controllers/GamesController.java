package org.quizstorage.director.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.quizstoradge.director.dto.AnswerResult;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstoradge.director.dto.GameResult;
import org.quizstorage.director.configurations.paths.ApiPaths;
import org.quizstorage.director.exceptions.QuizGameNotFound;
import org.quizstorage.director.services.GameDirector;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Service
public class GamesController {

    private final GameDirector gameDirector;

    @PostMapping(ApiPaths.GAMES_V1)
    @ResponseStatus(HttpStatus.CREATED)
    @HeadersAuth
    public GameInfo initGame(@RequestBody @Valid QuestionSet questionSet) {
        return gameDirector.newGame(questionSet);
    }

    @GetMapping(ApiPaths.CURRENT_QUESTION_V1)
    @HeadersAuth
    public ResponseEntity<GameQuestionDto> getCurrentQuestion() {
        return gameDirector.getCurrentQuestion()
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping(ApiPaths.ACCEPT_ANSWER_V1)
    @HeadersAuth
    public AnswerResult acceptAnswer(@PathVariable("gameId") String gameId,
                                     @PathVariable("questionNumber") int questionNumber,
                                     @RequestBody @NotEmpty Set<String> answers) {
        ObjectId objectId = new ObjectId(gameId);
        return gameDirector.acceptAnswers(objectId, questionNumber, answers);
    }

    @GetMapping(ApiPaths.GAME_RESULT_V1)
    @HeadersAuth
    public GameResult getGameResult(@PathVariable("gameId") String gameId) {
        ObjectId objectId = new ObjectId(gameId);
        return gameDirector.getGameResult(objectId).orElseThrow(() -> new QuizGameNotFound(objectId));
    }

    @GetMapping(ApiPaths.FINISHED_GAMES_V1)
    @HeadersAuth
    public List<GameInfo> findFinishedGames() {
        return gameDirector.findFinishedGames();
    }

}
