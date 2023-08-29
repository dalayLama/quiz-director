package org.quizstorage.director.mappers;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.quizstoradge.director.dto.*;
import org.quizstorage.director.dao.entities.GameQuestion;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.springframework.data.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface GameMapper {

    @Mapping(target = "gameInfo", source = "unansweredGameQuestion")
    @Mapping(target = "number", source = "unansweredGameQuestion.number")
    @Mapping(target = "question", source = "unansweredGameQuestion.question")
    @Mapping(target = "answers", source = "unansweredGameQuestion.answers")
    @Mapping(target = "category", source = "unansweredGameQuestion.category")
    @Mapping(target = "multiplyAnswers", source = "unansweredGameQuestion.multiplyAnswers")
    GameQuestionDto toGameQuestionDto(UnansweredGameQuestion unansweredGameQuestion);

    @Mapping(target = "gameInfo", source = "game")
    @Mapping(target = "number", source = "question.number")
    @Mapping(target = "question", source = "question.question")
    @Mapping(target = "answers", source = "question.answers")
    @Mapping(target = "category", source = "question.category")
    @Mapping(target = "multiplyAnswers", source = "question.multiplyAnswers")
    GameQuestionDto toGameQuestionDto(QuizGame game, GameQuestion question);

    @Mapping(target = "number", source = "question.number")
    @Mapping(target = "question", source = "question.question")
    @Mapping(target = "userAnswers", source = "question.userAnswers")
    @Mapping(target = "category", source = "question.category")
    @Mapping(target = "correct", source = "correct")
    QuestionResult toQuestionResult(GameQuestion question, boolean correct);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "sourceId", source = "sourceId")
    @Mapping(target = "start", source = "id.date")
    @Mapping(target = "end", source = "endDateTime")
    GameInfo toGameInfo(QuizGame game);

    List<GameInfo> toGameInfoList(Collection<QuizGame> quizGames);

    @Mapping(target = "id", source = "gameId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "start", source = "gameId.date")
    @Mapping(target = "sourceId", source = "sourceId")
    @Mapping(target = "end", ignore = true)
    GameInfo toGameInfo(UnansweredGameQuestion question);

    @Mapping(target = "gameInfo", source = "pair.first")
    @Mapping(target = "questionNumber", source = "pair.second.number")
    @Mapping(target = "nextQuestion", expression = "java( toGameQuestionDto(pair.getFirst(), nextQuestion) )")
    AnswerResult toAnswerResult(Pair<QuizGame, GameQuestion> pair, GameQuestion nextQuestion);

    default String toStringId(ObjectId objectId) {
        return Optional.ofNullable(objectId).map(Object::toString).orElse(null);
    }
}
