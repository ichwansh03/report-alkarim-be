package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.dto.request.AuthRequest;
import org.ichwan.dto.request.UserRequest;
import org.ichwan.exceptions.ConflictException;
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
    @Transactional
    public void register(AuthRequest req) {
        if (userRepository.findByRegnumber(req.regnumber()) != null) {
            throw new ConflictException("Account with registration number '" + req.regnumber() + "' already exists");
        }

        UserRequest userRequest = new UserRequest(req.name(), req.regnumber(), req.gender(), req.roles(), BcryptUtil.bcryptHash(req.password()), req.classRoomId());
        User user = mapper.mapToEntity(userRequest, User.class);
        userRepository.persist(user);
    }

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
