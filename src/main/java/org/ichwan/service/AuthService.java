package org.ichwan.service;

public interface AuthService {

    boolean authenticate(String rawPassword, String passwordHash);

    String generateAccessToken(String regnumber);
}
