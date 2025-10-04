package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.repository.UserRepository;

@ApplicationScoped
public class UserService implements org.ichwan.service.UserService<User> {

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public User register(User entity) {
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
