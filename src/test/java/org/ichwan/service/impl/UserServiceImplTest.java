package org.ichwan.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import org.ichwan.domain.User;
import org.ichwan.repository.UserRepository;
import org.ichwan.util.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.wildfly.security.util.PasswordUtil;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterThrowsIfExists() {
        User user = new User();
        user.setRegnumber("123");
        when(userRepository.findByRegnumber("123")).thenReturn(user);
        assertThrows(IllegalArgumentException.class, () -> userService.register(user));
    }

    @Test
    void testFindByRegnumber() {
        User user = new User();
        when(userRepository.findByRegnumber("123")).thenReturn(user);
        assertNotNull(userService.findByRegnumber("123"));
    }

    @Test
    void testFindById() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(user);
        assertNotNull(userService.finById(1L));
    }

    @Test
    void testFindByRoles() {
        when(userRepository.findByRoles(UserRole.STUDENT)).thenReturn(Collections.emptyList());
        List<User> users = userService.findByRoles("STUDENT");
        assertNotNull(users);
    }

    @Test
    void testAuthenticate() {
        String hash = BcryptUtil.bcryptHash("password");
        assertFalse(userService.authenticate("wrong", hash));
    }
}

