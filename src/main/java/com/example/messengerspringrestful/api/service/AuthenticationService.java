package com.example.messengerspringrestful.api.service;

import com.example.messengerspringrestful.api.domain.dto.JwtAuthenticationResponse;
import com.example.messengerspringrestful.api.domain.dto.LoginDtoRequest;
import com.example.messengerspringrestful.api.domain.dto.SignUpDtoRequest;
import com.example.messengerspringrestful.api.domain.model.ERole;
import com.example.messengerspringrestful.api.domain.model.InstaUserDetails;
import com.example.messengerspringrestful.api.domain.model.Profile;
import com.example.messengerspringrestful.api.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    /**
     * Регистрация пользователя
     *
     * @param signup данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpDtoRequest signup) {

        User user = User.builder()
                .username(signup.getUsername())
                .email(signup.getEmail())
                .password(passwordEncoder.encode(signup.getPassword()))
                .role(ERole.ROLE_USER)
                .userProfile(Profile.builder()
                        .displayName(signup.getName())
                        .profilePictureUrl(signup.getProfilePicUrl())
                        .build())
                .build();

        userService.create(user);

        InstaUserDetails instaUserDetails = new InstaUserDetails(user);

        String jwt = jwtService.generateToken(instaUserDetails);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(LoginDtoRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails userDetails = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        String jwt = jwtService.generateToken(userDetails);
        return new JwtAuthenticationResponse(jwt);
    }
}
