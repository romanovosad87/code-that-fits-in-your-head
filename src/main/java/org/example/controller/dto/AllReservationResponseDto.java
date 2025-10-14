package org.example.controller.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response object containing a list of reservations")
public record AllReservationResponseDto(

        @ArraySchema(
                arraySchema = @Schema(description = "List of reservations"),
                schema = @Schema(implementation = ReservationResponseDto.class)
        )
        List<ReservationResponseDto> reservations
) {
}
