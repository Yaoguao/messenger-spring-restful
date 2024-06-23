package com.example.messengerspringrestful.api.domain.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
public class JwtAuthenticationResponse {

    @NonNull
    private String accessToken;

    private String tokenType = "Bearer";
}
