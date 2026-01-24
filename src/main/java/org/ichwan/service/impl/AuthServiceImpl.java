package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.dto.UserResponse;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.AuthService;
import org.ichwan.util.MapperConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@ApplicationScoped
public class AuthServiceImpl implements AuthService<User> {

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Override
    @Transactional
    public void register(User entity) {
        if (userRepository.findByRegnumber(entity.getRegnumber()) != null) {
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

    @Override
    public boolean authenticate(String rawPassword, String regNumber) {

        return BcryptUtil.matches(rawPassword, userRepository.findByRegnumber(regNumber).getPassword());
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
