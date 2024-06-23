package com.example.messengerspringrestful.api.domain.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ApiResponse {

    private Boolean success;

    private String message;
}
