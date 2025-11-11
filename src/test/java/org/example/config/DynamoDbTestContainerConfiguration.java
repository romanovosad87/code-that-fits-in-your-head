package org.example.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false)
public class DynamoDbTestContainerConfiguration {

    @Container
    private static final LocalStackContainer localStackContainer = getlocalStackContainer();


    private static LocalStackContainer getlocalStackContainer() {
        var container = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.7"));
        return container
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .withReuse(true);
    }

    @Bean
    LocalStackContainer dynamoDbContainer() {
        return localStackContainer;
    }

    @DynamicPropertySource
    static void registerDynamoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamodb.endpoint",
                () -> localStackContainer.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString());
        registry.add("aws.dynamodb.region", localStackContainer::getRegion);
        registry.add("aws.access.key.id", localStackContainer::getAccessKey);
        registry.add("aws.secret.access.key", localStackContainer::getSecretKey);
    }
}
