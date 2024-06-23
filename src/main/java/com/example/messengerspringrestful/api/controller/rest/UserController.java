package com.example.messengerspringrestful.api.controller.rest;

import com.example.messengerspringrestful.api.domain.dto.UserProfileDto;
import com.example.messengerspringrestful.api.domain.dto.UserSummary;
import com.example.messengerspringrestful.api.domain.model.Address;
import com.example.messengerspringrestful.api.exception.ResourceNotFoundException;
import com.example.messengerspringrestful.api.domain.model.InstaUserDetails;
import com.example.messengerspringrestful.api.domain.model.User;
import com.example.messengerspringrestful.api.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@AllArgsConstructor
public class UserController {


    private UserService userService;

    public static final String USERS = "/users";

    public static final String FIND_USERS_SUMMARIES_NAME = "/users/summaries/names";
    public static final String FIND_USER = "/users/{username}";
    public static final String FIND_USERS_SUMMARIES = "/users/summaries";
    public static final String BY_ID_USER_PROFILE = "/users/profile/{id}";
    public static final String CURRENT_USER = "/users/me";
    public static final String CURRENT_USER_USERNAME = "/users/summary/{username}";


    @GetMapping(value = FIND_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@PathVariable("username") String username) {
        log.info("retrieving user {}", username);

        return  userService
                .findByUsername(username)
                .map(user -> ResponseEntity.ok(user))
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @GetMapping(value = USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        log.info("retrieving all users");

        return ResponseEntity
                .ok(userService.findAll());
    }

    @GetMapping(value = FIND_USERS_SUMMARIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllUserSummaries(
            @AuthenticationPrincipal InstaUserDetails userDetails) {
        log.info("retrieving all users summaries");

        return ResponseEntity.ok(userService
                .findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(userDetails.getUsername()))
                .map(this::convertTo));
    }

    @GetMapping(value = CURRENT_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public UserSummary getCurrentUser(@AuthenticationPrincipal InstaUserDetails userDetails) {
        return UserSummary
                .builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .name(userDetails.getUserProfile().getDisplayName())
                .profilePicture(userDetails.getUserProfile().getProfilePictureUrl())
                .build();
    }

    @GetMapping(value = CURRENT_USER_USERNAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserSummary(@PathVariable("username") String username) {
        log.info("retrieving user {}", username);

        return  userService
                .findByUsername(username)
                .map(user -> ResponseEntity.ok(convertTo(user)))
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @GetMapping(value = BY_ID_USER_PROFILE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByIdUserProfile(@PathVariable String id) {
        UserProfileDto userProfile = userService.findByIdUserProfile(id);

        log.info("user profile user {}", userProfile);

        return ResponseEntity.ok(userProfile);
    }

    @PutMapping(value = BY_ID_USER_PROFILE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateByUserProfile( @PathVariable String id,
                                                  @RequestBody UserProfileDto userProfileDto ) {

        log.info("user profile user {}", userProfileDto);

        return ResponseEntity
                .ok(userService.updateByUserProfile(id, userProfileDto));
    }

    @DeleteMapping(value = BY_ID_USER_PROFILE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteByUser(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("User delete: " + id );
    }

    @GetMapping(value = FIND_USERS_SUMMARIES_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findByUserProfile_DisplayName(@RequestBody String displayName) {

        List<UserSummary> userSummaries = userService.findByUserProfile_DisplayName(displayName)
                .stream()
                .map(this::convertTo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userSummaries);
    }

    @PostMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAddressByUser(@PathVariable String id, @RequestBody Address address) {

        return ResponseEntity.ok(userService.saveAddressByUser(id, address));
    }



    private UserSummary convertTo(User user) {
        return UserSummary
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getDisplayName())
                .profilePicture(user.getUserProfile().getProfilePictureUrl())
                .build();
    }
}
