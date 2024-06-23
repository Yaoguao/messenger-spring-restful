package com.example.messengerspringrestful.api.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDtoRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
