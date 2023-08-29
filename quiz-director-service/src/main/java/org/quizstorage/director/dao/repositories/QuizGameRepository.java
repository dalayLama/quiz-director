package org.quizstorage.director.dao.repositories;

import org.bson.types.ObjectId;
import org.quizstorage.director.dao.entities.QuizGame;
import org.quizstorage.director.dao.entities.UnansweredGameQuestion;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizGameRepository extends MongoRepository<QuizGame, ObjectId> {

    @Aggregation(pipeline = {
            "{ $match: { 'userId': '?0', 'endDateTime': null } }",
            "{ $sort: { '_id': 1 } }",
            "{ $limit: 1 }",
            "{ $unwind: { 'path': $questions} }",
            "{ $replaceRoot:  { 'newRoot': { '$mergeObjects': [ {'gameId': '$_id', 'userId': '$userId', 'sourceId': '$sourceId'}, '$questions' ] } } }",
            "{ $sort:  { 'number': 1 } }",
            "{ $match: { 'userAnswers': null } }",
            "{ $limit: 1 }"
    })
    Optional<UnansweredGameQuestion> findUnansweredQuestionForUser(String userId);

    @Query("{ userId: {$eq: ?0}, endDateTime: { $ne: null } }")
    List<QuizGame> findFinishedGamesForUser(String userId, Sort sort);

    @Query(value = "{ userId: ?0, endDateTime: null }", count = true)
    long countUnfinishedGamesForUser(String userId);

}
