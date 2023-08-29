package org.quizstorage.director.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.quizstoradge.director.dto.GameEventType;
import org.quizstoradge.director.dto.GameQuestionDto;
import org.quizstorage.director.configurations.WebSocketTestConfiguration;
import org.quizstorage.director.configurations.paths.WebSocketDestinations;
import org.quizstorage.director.containers.RabbitMQContainerConfiguration;
import org.quizstorage.director.controllers.WsController;
import org.quizstorage.director.services.GameDirector;
import org.quizstorage.director.utils.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

@SpringBootTest(classes = {WebSocketTestConfiguration.class, RabbitMQContainerConfiguration.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles({"test", "integration-test"})
@TestPropertySource(properties = {
        "containers.rabbitmq.config.enabled-plugins=rabbitmq_stomp,rabbitmq_web_stomp,rabbitmq_web_stomp_examples",
        "websocket.enable=true",
        "security.auth.websocket.test-interceptor.enable=true"
})
class WebSocketQuizGameNotifierTest {

    private WebSocketStompClient stompClient;

    @MockBean
    private GameDirector gameDirector;

    @Autowired
    private WebSocketQuizGameNotifier notifier;

    @Autowired
    private WebSocketTestConfiguration.Properties testProperties;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldConnect() {
        StompSession session = connect();
        assertThat(session.isConnected()).isTrue();
    }

    @Test
    void shouldAcceptMessagesToSendCurrentMessage() {
        StompSession session = connect();

        given(gameDirector.getCurrentQuestion()).willReturn(Optional.empty());

        session.send(WebSocketDestinations.SEND_CURRENT_QUESTION, Optional.empty());

        await()
                .atMost(1, SECONDS)
                .untilAsserted(() -> then(gameDirector).should(only()).getCurrentQuestion());
    }

    @Test
    void shouldAcceptAnswer() {
        StompSession session = connect();

        ObjectId gameId = new ObjectId("64defdd2933bac7a23678d90");
        WsController.ProcessingAnswersRequest request = new WsController.ProcessingAnswersRequest(
                gameId.toString(), 1, Set.of("answer"));

        session.send(WebSocketDestinations.PROCESS_ANSWER, request);

        await().atMost(1, SECONDS)
                .untilAsserted(() -> then(gameDirector).should(only())
                        .acceptAnswers(gameId, request.questionNumber(), request.answers()));
    }

    @Test
    void shouldSendGameEvent() throws InterruptedException {
        StompSession session = connect();

        WebSocketGameEvent event = new WebSocketGameEvent(TestData.GAME_INFO, GameEventType.STARTED_GAME);

        final BlockingQueue<WebSocketGameEvent> blockingQueue = new ArrayBlockingQueue<>(1);
        session.subscribe("/user/games.events", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketGameEvent.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((WebSocketGameEvent) payload);
            }
        });
        Thread.sleep(2000);
        notifier.onGameEvent(new GameEvent(this, TestData.GAME_INFO, GameEventType.STARTED_GAME));

        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> assertThat(blockingQueue.poll()).usingRecursiveComparison()
                        .isEqualTo(event));
    }

    @Test
    void shouldSendCurrentQuestion() throws InterruptedException {
        StompSession session = connect();

        GameQuestionDto currentQuestion = TestData.GAME_QUESTION_DTO;

        final BlockingQueue<GameQuestionDto> blockingQueue = new ArrayBlockingQueue<>(1);
        session.subscribe("/user/currentQuestion", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return GameQuestionDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                blockingQueue.add((GameQuestionDto) payload);
            }
        });
        Thread.sleep(2000);
        notifier.sendCurrentQuestion(currentQuestion);

        await()
                .atMost(2, SECONDS)
                .untilAsserted(() -> assertThat(blockingQueue.poll()).usingRecursiveComparison()
                        .isEqualTo(currentQuestion));
    }

    private StompSession connect() {
        SockJsClient sockJsClient = new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient())));
        stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(testProperties.getWebSocketTokenIdHeaderName(), WebSocketTestConfiguration.USER_TOKEN_ID.toString());
        CompletableFuture<StompSession> future = stompClient.connectAsync(getWebsocketUrl(),
                (WebSocketHttpHeaders) null, stompHeaders, new SilentHandler());
        return future.join();
    }

    private String getWebsocketUrl() {
        return "ws://%s:%d/sockjs".formatted("localhost", testProperties.getServerPort());
    }

    static class SilentHandler implements StompSessionHandler {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {

        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {

        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return null;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {

        }
    }

}