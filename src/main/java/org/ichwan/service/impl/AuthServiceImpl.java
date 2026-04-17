package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ichwan.domain.User;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.AuthService;
import org.ichwan.util.MapperConfig;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Override
    public boolean authenticate(String rawPassword, String regNumber) {
        User user = Optional.ofNullable(userRepository.findByRegnumber(regNumber))
                .orElseThrow(() -> new NotFoundException("Account with registration number '" + regNumber + "' not found"));

        return BcryptUtil.matches(rawPassword, user.getPassword());
    }

    @Override
    public String generateAccessToken(String regnumber){
        User user = userRepository.findByRegnumber(regnumber);
        return Jwt
                .issuer("report-alkarim-issuer")
                .subject(String.valueOf(user.getRegnumber()))
                .upn(user.getName())
                .groups(Set.of(user.getRoles().name()))
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign();
    }
}
