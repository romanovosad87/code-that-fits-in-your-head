package org.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReservationRequestDto(
        @Schema(
                description = "Date and time of the reservation (format: yyyy-MM-dd HH:mm)",
                example = "2025-03-15 18:30"
        )
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime at,

        @Schema(
                description = "Email address associated with the reservation",
                example = "martin.fowler@example.com"
        )
        String email,

        @Schema(
                description = "Name of the person who made the reservation",
                example = "Martin Fowler"
        )
        String name,

        @Schema(
                description = "Number of spots reserved",
                example = "2"
        )
        int quantity
) {
}
