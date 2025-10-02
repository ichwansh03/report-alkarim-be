package org.ichwan.service.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.User;
import org.ichwan.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class UserService implements org.ichwan.service.UserService<User> {

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public User register(User entity) {
        if (findByEmail(entity.getEmail()) != null) {
            throw new IllegalArgumentException("account already exists");
        }

        User user = new User();
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setClsroom(entity.getClsroom());
        user.setGender(entity.getGender());
        user.setRoles(entity.getRoles());
        user.setPassword(BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt(12)));
        userRepository.persist(user);
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean authenticate(String rawPassword, String passwordHash) {
        boolean checkpw = BCrypt.checkpw(rawPassword, passwordHash);
        Log.info(checkpw);
        return checkpw;
    }
}
