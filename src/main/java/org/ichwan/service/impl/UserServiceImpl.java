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
import org.ichwan.dto.response.PageResponse;
import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.UserResponse;
import org.ichwan.exceptions.BadRequestException;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.ClassRoomRepository;
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
    private ClassRoomRepository classRoomRepository;

    @Inject
    private MapperConfig mapper;

    @Transactional
    @Override
    public UserResponse update(UserRequest req, Long id) {
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        Optional.ofNullable(userRepository.findByRegnumber(req.regNumber()))
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new ConflictException("Registration number '" + req.regNumber() + "' already exists");
                    }
                });

        user.setName(req.name());
        user.setClassRoom(classRoomRepository.findById(req.classRoomId()));
        user.setGender(req.gender());
        user.setRegnumber(req.regNumber());
        user.setRoles(req.role());
        userRepository.persist(user);

        return mapper.map(user, UserResponse.class);
    }

    @CacheInvalidate(cacheName = "usersByRoles")
    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        userRepository.deleteById(id);
    }

    @Override
    public UserResponse findByRegnumber(String regnumber) {
        User user = Optional.ofNullable(userRepository.findByRegnumber(regnumber))
                .orElseThrow(() -> new NotFoundException("User with registration number '" + regnumber + "' not found"));

        return mapper.map(user, UserResponse.class);
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

    @Transactional
    @Override
    public UserResponse create(UserRequest req) {
        if (userRepository.findByRegnumber(req.regNumber()) != null) {
            throw new ConflictException("Account with registration number '" + req.regNumber() + "' already exists");
        }

        User user = mapper.mapToEntity(req, User.class);
        user.setPassword(BcryptUtil.bcryptHash(req.password()));
        userRepository.persist(user);

        return mapper.map(user, UserResponse.class);
    }

}
