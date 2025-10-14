package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.controller.dto.AllReservationResponseDto;
import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reservation")
public interface ReservationApi {

    @Operation(
            summary = "Create a new reservation",
            description = "Creates a new reservation and returns the created reservation details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.class)))
    })
    @PostMapping
    ResponseEntity<ReservationResponseDto> saveReservation(@RequestBody ReservationRequestDto dto);

    @Operation(
            summary = "Get reservation by ID",
            description = "Fetches details of a single reservation by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponseDto.class)))
    })
    @GetMapping("/{id}")
    ResponseEntity<ReservationResponseDto> getReservationById(@PathVariable String id);

    @Operation(
            summary = "Get all reservations by email",
            description = "Fetches all reservations associated with a given email address."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reservations for the given email",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AllReservationResponseDto.class)))
    })
    @GetMapping("/by-email/{email}")
    ResponseEntity<AllReservationResponseDto> getReservations(@PathVariable String email);
}
