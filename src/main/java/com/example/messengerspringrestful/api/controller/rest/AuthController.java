package com.example.messengerspringrestful.api.controller.rest;


import com.example.messengerspringrestful.api.dto.ApiResponse;
import com.example.messengerspringrestful.api.dto.JwtAuthenticationResponse;
import com.example.messengerspringrestful.api.dto.LoginDtoRequest;
import com.example.messengerspringrestful.api.dto.SignUpDtoRequest;
import com.example.messengerspringrestful.api.exception.BadRequestException;
import com.example.messengerspringrestful.api.exception.EmailAlreadyExistsException;
import com.example.messengerspringrestful.api.exception.UsernameAlreadyExistsException;
import com.example.messengerspringrestful.api.model.Profile;
import com.example.messengerspringrestful.api.model.Role;
import com.example.messengerspringrestful.api.model.User;
import com.example.messengerspringrestful.api.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDtoRequest loginRequest) {
        String token = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpDtoRequest signup) {
        log.info("creating user {}", signup.getUsername());

        User user = User.builder()
                .username(signup.getUsername())
                .email(signup.getEmail())
                .password(signup.getPassword())
                .userProfile(Profile.builder()
                        .displayName(signup.getName())
                        .profilePictureUrl(signup.getProfilePicUrl())
                        .build())
                .build();

        try {
            userService.registerUser(user, Role.USER);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            throw new BadRequestException(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
