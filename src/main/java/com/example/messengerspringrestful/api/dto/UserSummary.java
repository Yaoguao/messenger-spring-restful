package com.example.messengerspringrestful.api.dto;

import lombok.*;

@Data
@Builder
public class UserSummary {

    private String id;

    private String username;

    private String name;

    private String profilePicture;

}
