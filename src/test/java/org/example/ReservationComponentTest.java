package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationComponentTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("save reservation")
    @Test
    void saveReservation_ok() {
        //Arrange
        ReservationRequestDto request = new ReservationRequestDto(
                LocalDateTime.of(2025, 10, 10, 15, 0),
                "martin.fowler@example.com",
                "Martin Fowler",
                2);

        // Act
        var content = mockMvcTester.post()
                .uri("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(request));

        //Assert
        content.assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(ReservationResponseDto.class)
                .satisfies(response -> {
                    assertThat(response.id()).isNotEmpty();
                });
    }

    private String asJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
