package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.dto.UserResponse;
import org.ichwan.exceptions.BadRequestException;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.UserRepository;
import org.ichwan.util.MapperConfig;
import org.ichwan.util.UserRole;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserServiceImpl implements org.ichwan.service.UserService<User, UserResponse> {

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Transactional
    @Override
    public void update(User entity, Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        // Check if the new regnumber conflicts with another existing user
        Optional.ofNullable(userRepository.findByRegnumber(entity.getRegnumber()))
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Registration number '" + entity.getRegnumber() + "' already exists");
                    }
                });

        user.setName(entity.getName());
        user.setClsroom(entity.getClsroom());
        user.setGender(entity.getGender());
        user.setRegnumber(entity.getRegnumber());
        user.setRoles(entity.getRoles());
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            user.setPassword(BcryptUtil.bcryptHash(entity.getPassword()));
        }
        userRepository.persist(user);
    }

    @Override
    public UserResponse findByRegnumber(String regnumber) {
        User user = Optional.ofNullable(userRepository.findByRegnumber(regnumber))
                .orElseThrow(() -> new NotFoundException("User with registration number '" + regnumber + "' not found"));

        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse finById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        return mapper.map(user, UserResponse.class);
    }

    @Override
    public User findEntityById(Long id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }

    @Override
    public List<UserResponse> findByClsroomAndRoles(String classroom, String roles) {
        // Validate roles value before querying
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(roles.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role '" + roles + "'. Accepted values are: " +
                    Arrays.stream(UserRole.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
        }

        List<User> users = userRepository.findByClsroomAndRoles(classroom, userRole.name());
        if (users.isEmpty()) {
            throw new NotFoundException("No users found for classroom '" + classroom + "' with role '" + roles + "'");
        }

        return mapper.mapList(users, UserResponse.class);
    }

    @CacheResult(cacheName = "usersByRoles", lockTimeout = 3000)
    @Override
    public List<UserResponse> findByRoles(String roles) {
        // Validate roles value before querying
        UserRole userRole;
        try {
            userRole = UserRole.valueOf(roles.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role '" + roles + "'. Accepted values are: " +
                    Arrays.stream(UserRole.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")));
        }

        List<User> users = userRepository.findByRoles(userRole);
        if (users.isEmpty()) {
            throw new NotFoundException("No users found with role '" + roles + "'");
        }

        return mapper.mapList(users, UserResponse.class);
    }

    @CacheInvalidate(cacheName = "usersByRoles")
    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        userRepository.deleteById(id);
    }

}
