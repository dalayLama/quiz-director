package org.quizstorage.director.events;

import org.junit.jupiter.api.Test;
import org.quizstoradge.director.dto.QuizGameAnalysis;
import org.quizstorage.director.containers.RabbitMQContainerConfiguration;
import org.quizstorage.director.utils.TestData;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import wiremock.org.eclipse.jetty.util.BlockingArrayQueue;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RabbitMQContainerConfiguration.class})
@ActiveProfiles({"test", "integration-test"})
class RabbitQuizGameNotifierTest {

    private static final BlockingQueue<QuizGameAnalysis> FINISHED_GAMES_QUEUE = new BlockingArrayQueue<>();

    @Autowired
    private RabbitQuizGameNotifier notifier;

    @Test
    void shouldSendFinishedGame() throws InterruptedException {
        QuizGameAnalysis analysis = new QuizGameAnalysis(
                TestData.GAME_INFO,
                TestData.GAME_RESULT,
                List.of(TestData.CORRECT_QUESTION_RESULT)
        );
        notifier.onGameEvent(new GameEvent(this, analysis));

        assertThat(analysis)
                .usingRecursiveComparison()
                .isEqualTo(FINISHED_GAMES_QUEUE.poll(10, TimeUnit.SECONDS));
    }

    @RabbitListener(queues = "${rabbitmq.queues.finished-games-queue}")
    public void finishedGamesListener(QuizGameAnalysis analysis) {
        FINISHED_GAMES_QUEUE.add(analysis);
    }

}