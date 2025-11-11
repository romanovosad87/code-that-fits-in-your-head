package org.example.config;

import org.example.entity.Reservation;
import org.example.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.extensions.VersionedRecordExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.region}")
    private String region;


    @SuppressWarnings("java:S6242")
    @Bean
    @Profile("!local && !test")
    public DynamoDbClient dynamoDbClientD() {
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    @Profile("local | test")
    public DynamoDbClient dynamoDbClientLocal(@Value("${aws.secret.access.key}") String secretAccessKey,
                                              @Value("${aws.access.key.id}") String accessKeyId,
                                              @Value("${aws.dynamodb.endpoint}") String endpoint) {
        return DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient getDynamoDbEnhancedClient(DynamoDbClient dbClient) {
        return DynamoDbEnhancedClient
                .builder()
                .extensions(VersionedRecordExtension.builder().build())
                .dynamoDbClient(dbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<Reservation> reservationTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(Constants.RESERVATION_TABLE_NAME, Reservation.TABLE_SCHEMA);
    }
}
