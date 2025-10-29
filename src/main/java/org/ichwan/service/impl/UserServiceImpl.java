package org.ichwan.service.impl;

import io.quarkus.cache.CacheResult;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.repository.UserRepository;
import org.ichwan.util.UserRole;

import java.util.List;

@ApplicationScoped
public class UserServiceImpl implements org.ichwan.service.UserService<User> {

    @Inject
    private UserRepository userRepository;

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
    public User findByRegnumber(String regnumber) {
        return userRepository.findByRegnumber(regnumber);
    }

    @Override
    public User finById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByClsroomAndRoles(String classroom, String roles) {
        return userRepository.findByClsroomAndRoles(classroom, roles);
    }

    @CacheResult(cacheName = "usersByRoles")
    @Override
    public List<User> findByRoles(String roles) {
        return userRepository.findByRoles(UserRole.valueOf(roles.toUpperCase()));
    }

    @Override
    public boolean authenticate(String rawPassword, String passwordHash) {

        return BcryptUtil.matches(rawPassword, passwordHash);
    }
}
