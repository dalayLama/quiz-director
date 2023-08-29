package org.quizstorage.director.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quizstoradge.director.dto.GameEventType;
import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitQuizGameNotifier {

    private final RabbitTemplate rabbitTemplate;

    @EventListener(classes = GameEvent.class)
    public void onGameEvent(GameEvent event) {
        if (event.getEventType() != GameEventType.FINISHED_GAME) {
            return;
        }
        Optional.ofNullable(event.getQuizGameAnalysis()).ifPresentOrElse(
                this::notify,
                () -> log.warn("The analysis of the finished game \"{}\" is null", event.getGameInfo().id())
        );
    }

    private void notify(QuizGameAnalysis quizGameAnalysis) {
        rabbitTemplate.convertAndSend(GameEventType.FINISHED_GAME.name(), quizGameAnalysis);
        log.info("Detailed info of the game \"{}\" was sent to the rabbit queue", quizGameAnalysis.gameInfo().id());
    }

}
