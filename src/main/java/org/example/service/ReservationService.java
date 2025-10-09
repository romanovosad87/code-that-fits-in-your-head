package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.example.entity.Reservation;
import org.example.mapper.ReservationMapper;
import org.example.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final ReservationRepository reservationRepository;

    public ReservationResponseDto saveReservation(ReservationRequestDto dto) {
        Reservation reservation = reservationMapper.toEntity(dto);
        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    public List<ReservationResponseDto> getReservationsByEmail(String email) {
        return reservationRepository.getReservationsByEmail(email)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    public ReservationResponseDto getReservationById(String id) {
        return reservationMapper.toResponse(reservationRepository.getReservationById(id));
    }
}
