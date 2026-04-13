package org.ichwan.service;

import org.ichwan.dto.request.AuthRequest;

public interface AuthService {

    void register(AuthRequest entity);

    boolean authenticate(String rawPassword, String passwordHash);

    String generateAccessToken(String regnumber);
}
