package org.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response object containing details of a reservation")
public record ReservationResponseDto(
        @Schema(
                description = "Unique identifier of the reservation",
                example = "b17f6a3e-4b6e-4b3a-a1e9-8f6a89a5ef93"
        )
        String id,

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
