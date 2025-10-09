package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reservation {
    private String id;
    private LocalDateTime at;
    private String email;
    private String name;
    private int quantity;
}
