package org.example.component;

import org.example.config.ComponentTestConfiguration;
import org.example.controller.dto.AllReservationResponseDto;
import org.example.controller.dto.ReservationRequestDto;
import org.example.controller.dto.ReservationResponseDto;
import org.example.entity.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@ComponentTestConfiguration
class ReservationComponentTest extends BaseComponentTest {

    private static final String SAVE_RESERVATION_DATA = "data/save_reservation_ok.json";
    private static final String DATA_RESERVATIONS = "data/reservations.json";

    @Autowired
    private MockMvcTester mockMvcTester;

    @DisplayName("save reservation")
    @Test
    void saveReservation_ok() {
        //Arrange
        String request = getContentByString(SAVE_RESERVATION_DATA, ReservationRequestDto.class);

        // Act
        var content = mockMvcTester.post()
                .uri("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request);

        //Assert
        content.assertThat()
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .convertTo(ReservationResponseDto.class)
                .satisfies(response -> {
                    assertThat(response.id()).isNotEmpty();
                });
    }

    @DisplayName("get reservation by id")
    @Test
    void getReservationById() {
        //Arrange
        String id = "61c7a83e-17ce-43f5-9e86-30e5357832fd";
        saveTestListData(DATA_RESERVATIONS, Reservation.class, reservationDynamoDbTable);

        // Act
        var request = mockMvcTester.get()
                .uri("/reservation/" + id);

        //Assert
        request.assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(ReservationResponseDto.class)
                .satisfies(response -> assertThat(response.id()).isEqualTo(id));
    }

    @DisplayName("get reservation by email")
    @Test
    void getReservationByEmail() {
        //Arrange
        String email = "martin.fowler@example.com";
        saveTestListData(DATA_RESERVATIONS, Reservation.class, reservationDynamoDbTable);

        // Act
        var request = mockMvcTester.get()
                .uri("/reservation/by-email/" + email);

        //Assert
        request.assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .convertTo(AllReservationResponseDto.class)
                .satisfies(response -> {
                    var reservations = response.reservations();
                    assertThat(reservations).isNotEmpty();
                    assertThat(reservations).hasSize(1);
                    assertThat(reservations.getFirst().email()).isEqualTo(email);
                });
    }
}
