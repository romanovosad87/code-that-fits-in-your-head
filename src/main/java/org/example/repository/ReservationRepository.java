package org.example.repository;

import org.example.entity.Reservation;

import java.util.List;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> getReservationsByEmail(String email);

    Reservation getReservationById(String id);
}
