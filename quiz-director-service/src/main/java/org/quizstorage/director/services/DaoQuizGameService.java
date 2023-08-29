package org.quizstorage.director.services;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.repositories.QuizGameRepository;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.quizstorage.director.exceptions.GameIsFinishedException;
import org.quizstorage.director.exceptions.GameNotFoundException;
import org.quizstorage.director.exceptions.GameQuestionAlreadyHasAnswer;
import org.quizstorage.director.utils.QuizGameUtil;
import org.quizstorage.generator.dto.Question;
import org.quizstorage.generator.dto.QuestionSet;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Validated
public class DaoQuizGameService implements QuizGameService {

    private final QuizGameRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizGame> findById(ObjectId gameId) {
        return repository.findById(gameId);
    }

    @Override
    @Transactional
    public QuizGame newGame(@NotBlank String userId, @NotNull @Valid QuestionSet questionSet) {
        QuizGame newGame = QuizGame.builder()
                .sourceId(questionSet.sourceId())
                .userId(userId)
                .questions(newGameQuestions(questionSet.questions()))
                .build();
        return repository.save(newGame);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UnansweredGameQuestion> findUnansweredQuestion(@NotNull String userId) {
        return repository.findUnansweredQuestionForUser(userId);
    }

    @Override
    @Transactional
    public Pair<QuizGame, GameQuestion> setAnswers(@NotNull ObjectId gameId,
                                                   @Positive int questionNumber,
                                                   @NotEmpty Set<String> answer) {
        QuizGame game = getGame(gameId);
        if (game.isFinished()) {
            throw new GameIsFinishedException(gameId);
        }
        GameQuestion question = game.getQuestionByNumber(questionNumber);
        setUserAnswer(game, question, answer);
        repository.save(game);
        return Pair.of(game, question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizGame> findFinishedGames(@NotBlank String userId) {
        return repository.findFinishedGamesForUser(userId, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userHasUnfinishedGames(String userId) {
        return repository.countUnfinishedGamesForUser(userId) > 0;
    }

    private void setUserAnswer(QuizGame game, GameQuestion gameQuestion, Set<String> answers) {
        if (gameQuestion.isAnswered()) {
            throw new GameQuestionAlreadyHasAnswer(game.getId(), gameQuestion.getNumber());
        }
        gameQuestion.setUserAnswers(answers);
        gameQuestion.setAnswerDateTime(Instant.now());
        if (!hasUnansweredQuestions(game)) {
            game.setEndDateTime(Instant.now());
        }
    }

    private boolean hasUnansweredQuestions(QuizGame game) {
        return QuizGameUtil.hasUnansweredQuestions(game);
    }

    private QuizGame getGame(ObjectId gameId) {
        return repository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    private List<GameQuestion> newGameQuestions(List<Question> questions) {
        return IntStream
                .range(0, questions.size())
                .mapToObj(index -> newGameQuestion(index + 1, questions.get(index)))
                .toList();
    }

    private GameQuestion newGameQuestion(int questionNumber, Question question) {
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
