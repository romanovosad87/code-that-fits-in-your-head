package org.example.entity;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDateTime;

@DynamoDbBean
@Data
public class Reservation {

    public static final TableSchema<Reservation> TABLE_SCHEMA = TableSchema.fromBean(Reservation.class);
    public static final String EMAIL_INDEX = "EmailIndex";

    private String id;
    private LocalDateTime at;
    private String email;
    private String name;
    private int quantity;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames =  EMAIL_INDEX)
    public String getEmail() {
        return email;
    }
}
