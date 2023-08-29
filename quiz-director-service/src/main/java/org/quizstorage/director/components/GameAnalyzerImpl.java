package org.quizstorage.director.components;

import lombok.RequiredArgsConstructor;
import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.quizstoradge.director.dto.GameInfo;
import org.quizstoradge.director.dto.GameResult;
import org.quizstoradge.director.dto.QuestionResult;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.mappers.GameMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GameAnalyzerImpl implements GameAnalyzer {

    private final GameMapper gameMapper;

    @Override
    public QuizGameAnalysis analyze(QuizGame game) {
        List<QuestionResult> questionResults = game.getQuestions().stream()
                .map(this::toQuestionResult)
                .toList();
        GameResult gameResult = calculateResult(questionResults);
        GameInfo gameInfo = gameMapper.toGameInfo(game);
        return new QuizGameAnalysis(gameInfo, gameResult, questionResults);
    }

    @Override
    public GameResult calculateResult(QuizGame game) {
        Integer correctAnswers = game.getQuestions().stream()
                .filter(GameQuestion::isAnswered)
                .filter(this::hasCorrectAnswer)
                .map(e -> 1)
                .reduce(0, Integer::sum);
        return new GameResult(game.getQuestions().size(), correctAnswers);
    }

    @Override
    public boolean hasCorrectAnswer(GameQuestion gameQuestion) {
        Set<String> correctAnswers = gameQuestion.getCorrectAnswers();
        Set<String> userAnswers = gameQuestion.getUserAnswers();
        return Objects.equals(correctAnswers.size(), userAnswers.size()) &&
                correctAnswers.containsAll(userAnswers);
    }

    private GameResult calculateResult(Collection<QuestionResult> questionResults) {
        Integer correctAnswers = questionResults.stream()
                .filter(QuestionResult::correct)
                .map(r -> 1)
                .reduce(0, Integer::sum);
        return new GameResult(questionResults.size(), correctAnswers);
    }

    private QuestionResult toQuestionResult(GameQuestion question) {
        return gameMapper.toQuestionResult(question, hasCorrectAnswer(question));
    }

}
