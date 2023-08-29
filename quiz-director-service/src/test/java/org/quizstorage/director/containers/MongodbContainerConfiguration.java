package org.quizstorage.director.containers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers
public class MongodbContainerConfiguration {

    @Bean
    @ServiceConnection
    @RestartScope
    @SuppressWarnings("resource")
    public MongoDBContainer mongoDBContainer(final MongoContainerProperties properties, Network network) {
        return new MongoDBContainer(properties.image)
                .withNetwork(network)
                .withNetworkAliases(new String[]{"mongodb"})
                .withExposedPorts(properties.port);
    }


    @Bean
    @ConditionalOnMissingBean
    public PlatformTransactionManager mongoTransactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    @RestartScope
    public Network mongoNetwork() {
        return Network.builder().driver("bridge").build();
    }

    @TestComponent
    public static class MongoContainerProperties {

        @Value("${mongo-container.image:mongo:latest}")
        private String image;

        @Value("${mongo-container.port:27017}")
        private int port;

    }

}
