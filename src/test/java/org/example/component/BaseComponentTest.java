package org.example.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.example.config.DynamoDbTestContainerConfiguration;
import org.example.entity.Reservation;
import org.example.util.TestDataLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.example.util.Constants.RESERVATION_TABLE_NAME;

@Slf4j
@SpringBootTest
@Import(DynamoDbTestContainerConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class BaseComponentTest {

    @Autowired
    protected DynamoDbClient dynamoDbClient;

    @Autowired
    protected DynamoDbTable<Reservation> reservationDynamoDbTable;

    @Autowired
    private TestDataLoader testDataLoader;

    @BeforeAll
    void setupTable() {
        createReservationTableIfNotExists(dynamoDbClient);
    }

    @BeforeEach
    void cleanTables() {
        reservationDynamoDbTable.scan().items().forEach(reservationDynamoDbTable::deleteItem);
    }

    protected <T> void saveTestData(String jsonPath, Class<T> clazz, DynamoDbTable<T> table) {
        testDataLoader.loadAndSave(jsonPath, clazz, table);
    }

    protected <T> void saveTestListData(String jsonPath, Class<T> clazz, DynamoDbTable<T> table) {
        testDataLoader.loadAndSaveList(jsonPath, clazz, table);
    }

    protected  <T> String getContentByString(String filePath, Class<T> clazz) {
        return testDataLoader.getContentByString(filePath, clazz);
    }

    private void createReservationTableIfNotExists(DynamoDbClient client) {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(RESERVATION_TABLE_NAME)
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName("id")
                                    .attributeType(ScalarAttributeType.S)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName("email")
                                    .attributeType(ScalarAttributeType.S)
                                    .build()
                    )
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName("id")
                                    .keyType(KeyType.HASH)
                                    .build()
                    )
                    .globalSecondaryIndexes(
                            GlobalSecondaryIndex.builder()
                                    .indexName(Reservation.EMAIL_INDEX)
                                    .keySchema(
                                            KeySchemaElement.builder()
                                                    .attributeName("email")
                                                    .keyType(KeyType.HASH)
                                                    .build()
                                    )
                                    .projection(Projection.builder()
                                            .projectionType(ProjectionType.ALL)
                                            .build())
                                    .provisionedThroughput(ProvisionedThroughput.builder()
                                            .readCapacityUnits(5L)
                                            .writeCapacityUnits(5L)
                                            .build())
                                    .build()
                    )
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .build();

            client.createTable(request);
            client.waiter().waitUntilTableExists(r -> r.tableName(RESERVATION_TABLE_NAME));

            log.info("✅ Created table '" + RESERVATION_TABLE_NAME
                    + "' with GSI: " + Reservation.EMAIL_INDEX);

        } catch (ResourceInUseException e) {
            log.info("ℹ️ Table '" + RESERVATION_TABLE_NAME + "' already exists.");
        }
    }
}
