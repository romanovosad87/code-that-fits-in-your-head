package org.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ReservationRequestDto(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime at,
        String email,
        String name,
        int quantity
) {
}
