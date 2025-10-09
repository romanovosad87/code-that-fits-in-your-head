package org.example.repository;

import org.example.entity.Reservation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReservationRepositoryImpl implements ReservationRepository {

    private final Map<String, Reservation> reservationMap = new ConcurrentHashMap<>();

    @Override
    public Reservation save(Reservation reservation) {
        reservationMap.put(reservation.getId(), reservation);
        return reservation;
    }

    @Override
    public List<Reservation> getReservationsByEmail(String email) {
        return reservationMap.values().stream()
                .filter(reservation -> reservation.getEmail().equals(email))
                .toList();
    }

    @Override
    public Reservation getReservationById(String id) {
        return reservationMap.get(id);
    }
}
