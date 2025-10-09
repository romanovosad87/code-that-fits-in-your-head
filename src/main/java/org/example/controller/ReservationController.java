package org.example.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;


    @PostMapping
    public ResponseEntity<ReservationResponseDto> saveReservation(@RequestBody ReservationRequestDto dto) {
        ReservationResponseDto responseDto = reservationService.saveReservation(dto);
        return ResponseEntity
                .created(getLocation(responseDto.id()))
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservationById(@PathVariable String id) {
        return new ResponseEntity<>(reservationService.getReservationById(id), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<ReservationResponseDto>> getReservations(@PathVariable String email) {
        return new ResponseEntity<>(reservationService.getReservationsByEmail(email), HttpStatus.OK);
    }

    private URI getLocation(String id) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
