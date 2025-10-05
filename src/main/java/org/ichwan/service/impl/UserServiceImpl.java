package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.dto.AuthRequest;
import org.ichwan.repository.UserRepository;

@ApplicationScoped
public class UserServiceImpl implements org.ichwan.service.UserService<User, AuthRequest> {

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public User register(AuthRequest request) {
        if (findByRegnumber(request.regnumber()) != null) {
            throw new IllegalArgumentException("account already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setRegnumber(request.regnumber());
        user.setClsroom(request.clsroom());
        user.setGender(request.gender());
        user.setRoles(request.roles());
        user.setPassword(BcryptUtil.bcryptHash(request.password()));
        userRepository.persist(user);
        return user;
    }

    @Override
    public User findByRegnumber(String regnumber) {
        return userRepository.findByRegnumber(regnumber);
    }

    @Override
    public boolean authenticate(String rawPassword, String passwordHash) {

        return BcryptUtil.matches(rawPassword, passwordHash);
    }
}
