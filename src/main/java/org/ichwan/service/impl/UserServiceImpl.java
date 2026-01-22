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
import org.ichwan.repository.UserRepository;
import org.ichwan.util.MapperConfig;
import org.ichwan.util.UserRole;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class UserServiceImpl implements org.ichwan.service.UserService<User, UserResponse> {

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Override
    @Transactional
    public void register(User entity) {
        if (findByRegnumber(entity.getRegnumber()) != null) {
            throw new IllegalArgumentException("account already exists");
        }

        User user = new User();
        user.setName(entity.getName());
        user.setRegnumber(entity.getRegnumber());
        user.setClsroom(entity.getClsroom());
        user.setGender(entity.getGender());
        user.setRoles(entity.getRoles());
        user.setPassword(BcryptUtil.bcryptHash(entity.getPassword()));
        userRepository.persist(user);

    }

    @Transactional
    @Override
    public void update(User entity, Long id) {
        User user = userRepository.findById(id);
        if (user != null) {
            user.setName(entity.getName());
            user.setClsroom(entity.getClsroom());
            user.setGender(entity.getGender());
            user.setRegnumber(entity.getRegnumber());
            user.setRoles(entity.getRoles());
            if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
                user.setPassword(BcryptUtil.bcryptHash(entity.getPassword()));
            }
            userRepository.persist(user);
        } else {
            throw new IllegalArgumentException("user not found");
        }
    }

    @Override
    public UserResponse findByRegnumber(String regnumber) {
        User user = userRepository.findByRegnumber(regnumber);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse finById(Long id) {
        User user = userRepository.findById(id);
        return mapper.map(user, UserResponse.class);
    }

    @Override
    public User findEntityById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByClsroomAndRoles(String classroom, String roles) {
        return userRepository.findByClsroomAndRoles(classroom, roles);
    }

    @CacheResult(cacheName = "usersByRoles", lockTimeout = 3000)
    @Override
    public List<User> findByRoles(String roles) {
        return userRepository.findByRoles(UserRole.valueOf(roles.toUpperCase()));
    }

    @Override
    public boolean authenticate(String rawPassword, String regNumber) {

        return BcryptUtil.matches(rawPassword, userRepository.findByRegnumber(regNumber).getPassword());
    }

    @CacheInvalidate(cacheName = "usersByRoles")
    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public String generateAccessToken(UserResponse user) {
        return Jwt
                .issuer("report-alkarim-issuer")
                .subject(String.valueOf(user.regnumber()))
                .upn(user.name())
                .groups(Set.of(user.roles().name()))
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign();
    }
}
