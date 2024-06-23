package com.example.messengerspringrestful.api.domain.dto;

import com.example.messengerspringrestful.api.domain.model.Profile;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserProfileDto {

    private String username;

    private String email;

    private Instant createdAt;

    private Instant updatedAt;

    private Profile userProfile;
}
