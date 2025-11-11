package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.Reservation;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ReservationRepositoryImpl implements ReservationRepository {

    private final DynamoDbTable<Reservation> reservationDynamoDbTableTable;

    @Override
    public Reservation save(Reservation reservation) {
        reservationDynamoDbTableTable.putItem(reservation);
        return reservation;
    }

    @Override
    public List<Reservation> getReservationsByEmail(String email) {
        DynamoDbIndex<Reservation> emailIndex = reservationDynamoDbTableTable.index(Reservation.EMAIL_INDEX);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(email)
                .build());

        return emailIndex.query(queryConditional)
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    @Override
    public Reservation getReservationById(String id) {
        return reservationDynamoDbTableTable.getItem(
                Key.builder()
                        .partitionValue(id)
                        .build());
    }
}
