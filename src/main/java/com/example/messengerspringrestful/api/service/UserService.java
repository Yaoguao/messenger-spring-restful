package com.example.messengerspringrestful.api.service;

import com.example.messengerspringrestful.api.domain.dto.UserProfileDto;
import com.example.messengerspringrestful.api.domain.model.Address;
import com.example.messengerspringrestful.api.domain.model.Profile;
import com.example.messengerspringrestful.api.exception.EmailAlreadyExistsException;
import com.example.messengerspringrestful.api.exception.UsernameAlreadyExistsException;
import com.example.messengerspringrestful.api.domain.model.InstaUserDetails;
import com.example.messengerspringrestful.api.domain.model.User;
import com.example.messengerspringrestful.api.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервисный класс для управления пользователями.
 * Обеспечивает создание, поиск и обновление пользователей в системе.
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    /**
     * Конструктор класса UserService.
     *
     * @param userRepository Репозиторий для доступа к данным пользователей.
     */
    private final UserRepository userRepository;


    /**
     * Создает нового пользователя в системе.
     *
     * @param user Пользователь для регистрации.
     * @return Созданный пользователь.
     * @throws UsernameAlreadyExistsException если имя пользователя уже занято.
     * @throws EmailAlreadyExistsException если email уже занят другим пользователем.
     */
    public User create(User user) {
        log.info("registering user {}", user.getUsername());

        if(userRepository.existsByUsername(user.getUsername())) {
            log.warn("username {} already exists.", user.getUsername());

            throw new UsernameAlreadyExistsException(
                    String.format("username %s already exists", user.getUsername()));
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            log.warn("email {} already exists.", user.getEmail());

            throw new EmailAlreadyExistsException(
                    String.format("email %s already exists", user.getEmail()));
        }
        user.setActive(true);

        return userRepository.save(user);
    }

    /**
     * Получение всех пользователей
     *
     * @return пользователи
     */
    public List<User> findAll() {
        log.info("retrieving all users");
        return userRepository.findAll();
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public Optional<User> findByUsername(String username) {
        log.info("retrieving user {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Ищет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя для поиска.
     * @return Optional с пользователем, если найден.
     */
    public Optional<User> findById(String id) {
        log.info("retrieving user {}", id);
        return userRepository.findById(id);
    }

    /**
     * Возвращает объект UserDetailsService для Spring Security.
     * Используется для аутентификации и авторизации пользователей.
     *
     * @return UserDetailsService для Spring Security.
     */
    public UserDetailsService userDetailsService() {
        return this::instaUserDetails;
    }

    /**
     * Получает текущего аутентифицированного пользователя.
     *
     * @return Optional с текущим пользователем.
     */
    public Optional<User> getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    /**
     * Возвращает объект InstaUserDetails для заданного пользователя.
     * Используется для предоставления деталей пользователя для Spring Security.
     *
     * @param username Имя пользователя для поиска.
     * @return InstaUserDetails для заданного пользователя.
     */
    public InstaUserDetails instaUserDetails(String username) {
        log.info("retrieving user {}", username);
        return new InstaUserDetails(findByUsername(username).orElseThrow());
    }

    /**
     * Получает профиль пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя для поиска профиля.
     * @return Профиль пользователя в виде UserProfileDto.
     */
    public UserProfileDto findByIdUserProfile(String id) {
        User user = userRepository.findById(id).orElseThrow();
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .userProfile(user.getUserProfile())
                .build();

        return userProfileDto;
    }

    /**
     * Обновляет информацию о пользователе на основе данных из UserProfileDto.
     *
     * @param id            Идентификатор пользователя для обновления.
     * @param userProfileDto Данные профиля пользователя для обновления.
     * @return Обновленный объект пользователя.
     */
    public User updateByUserProfile(String id, UserProfileDto userProfileDto) {

        User user = userRepository.findById(id).orElseThrow();

        user.setUsername(userProfileDto.getUsername());
        user.setEmail(userProfileDto.getEmail());
        user.setUserProfile(userProfileDto.getUserProfile());

        return userRepository.save(user);
    }

    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

    public List<User> findByUserProfile_DisplayName(String displayName) {
        return userRepository.findByUserProfile_DisplayName(displayName);
    }


    public User saveAddressByUser(String id, Address address) {
        User user = userRepository.findById(id).orElseThrow();

        Profile userProfile = user.getUserProfile();

        if (userProfile.getAddresses() == null) {
            HashSet<Address> addresses = new HashSet<>();
            addresses.add(address);
            userProfile.setAddresses(addresses);
        } else {
            Set<Address> addresses = userProfile.getAddresses();
            addresses.add(address);
            userProfile.setAddresses(addresses);
        }

        user.setUserProfile(userProfile);

        return userRepository.save(user);
    }
}
