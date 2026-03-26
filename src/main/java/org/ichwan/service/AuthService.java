package org.ichwan.service;

import org.ichwan.dto.AuthRequest;

public interface AuthService {

    void register(AuthRequest entity);

    boolean authenticate(String rawPassword, String passwordHash);
}
