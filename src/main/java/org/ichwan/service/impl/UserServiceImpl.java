package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.dto.PageResponse;
import org.ichwan.dto.UserRequest;
import org.ichwan.dto.UserResponse;
import org.ichwan.exceptions.BadRequestException;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.UserService;
import org.ichwan.util.MapperConfig;
import org.ichwan.util.UserRole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Transactional
    @Override
    public void update(UserRequest req, Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        Optional.ofNullable(userRepository.findByRegnumber(req.regnumber()))
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Registration number '" + req.regnumber() + "' already exists");
                    }
                });

        user.setName(req.name());
        user.setClsroom(req.clsroom());
        user.setGender(req.gender());
        user.setRegnumber(req.regnumber());
        user.setRoles(req.roles());
        userRepository.persist(user);
    }

    @Override
    public UserResponse findByRegnumber(String regnumber) {
        User user = Optional.ofNullable(userRepository.findByRegnumber(regnumber))
                .orElseThrow(() -> new NotFoundException("User with registration number '" + regnumber + "' not found"));

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

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        return mapper.map(user, UserResponse.class);
    }

    @Override
    public PageResponse<UserResponse> getAll(int page, int size) {
        PanacheQuery<User> query = userRepository.findAll();
        query.page(Page.of(page, size));

        List<User> users = query.list();
        if (users.isEmpty()) {
            throw new NotFoundException("No users found");
        }

        long totalItems = query.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<UserResponse> data = mapper.mapList(users, UserResponse.class);

        return new PageResponse<>(data, page, size, totalItems, totalPages);
    }

}
