package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.controller.dto.AllReservationResponseDto;
import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.example.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReservationController implements ReservationApi {

    private final ReservationService reservationService;

    @Override
    public ResponseEntity<ReservationResponseDto> saveReservation(ReservationRequestDto dto) {
        ReservationResponseDto responseDto = reservationService.saveReservation(dto);
        return ResponseEntity
                .created(getLocation(responseDto.id()))
                .body(responseDto);
    }

    @Override
    public ResponseEntity<ReservationResponseDto> getReservationById(String id) {
        return new ResponseEntity<>(reservationService.getReservationById(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AllReservationResponseDto> getReservations(String email) {
        List<ReservationResponseDto> reservations = reservationService.getReservationsByEmail(email);
        return new ResponseEntity<>(new AllReservationResponseDto(reservations), HttpStatus.OK);
    }

    private URI getLocation(String id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
