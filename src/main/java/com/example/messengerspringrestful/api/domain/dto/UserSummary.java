package com.example.messengerspringrestful.api.domain.dto;

import lombok.*;

@Data
@Builder
public class UserSummary {

    private String id;

    private String username;

    private String name;

    private String profilePicture;

}
