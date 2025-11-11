package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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
