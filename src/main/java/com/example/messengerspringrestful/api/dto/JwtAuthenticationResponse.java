package com.example.messengerspringrestful.api.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
public class JwtAuthenticationResponse {

    @NonNull
    private String accessToken;

    private String tokenType = "Bearer";
}
