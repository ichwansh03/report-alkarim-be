package org.ichwan.service;

public interface AuthService<E> {

    void register(E entity);

    boolean authenticate(String rawPassword, String passwordHash);
}
