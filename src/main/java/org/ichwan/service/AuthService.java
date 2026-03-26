package org.ichwan.service;

import org.ichwan.dto.AuthRequest;
import org.ichwan.dto.UserResponse;

public interface AuthService {

    void register(AuthRequest entity);

    boolean authenticate(String rawPassword, String passwordHash);

    String generateAccessToken(UserResponse user);
}
