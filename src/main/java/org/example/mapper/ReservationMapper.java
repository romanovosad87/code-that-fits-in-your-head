package org.example.mapper;

import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.example.entity.Reservation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequestDto dto) {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setAt(dto.at());
        reservation.setName(dto.name());
        reservation.setEmail(dto.email());
        reservation.setQuantity(dto.quantity());
        return reservation;
    }

    public ReservationResponseDto toResponse(Reservation reservation) {
        return new ReservationResponseDto(
                reservation.getId(),
                reservation.getAt(),
                reservation.getEmail(),
                reservation.getName(),
                reservation.getQuantity()
        );
    }
}
