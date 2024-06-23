package com.example.messengerspringrestful.api.controller.rest;


import com.example.messengerspringrestful.api.domain.dto.JwtAuthenticationResponse;
import com.example.messengerspringrestful.api.domain.dto.LoginDtoRequest;
import com.example.messengerspringrestful.api.domain.dto.SignUpDtoRequest;
import com.example.messengerspringrestful.api.service.AuthenticationService;
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

@RestController
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthController {

    final AuthenticationService authenticationService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDtoRequest loginDtoRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signIn(loginDtoRequest);
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpDtoRequest signUpDtoRequest) {
        log.info("creating user {}", signUpDtoRequest.getUsername());

        JwtAuthenticationResponse jwtAuthenticationResponse = authenticationService.signUp(signUpDtoRequest);

        return ResponseEntity.ok(jwtAuthenticationResponse);
    }
}
