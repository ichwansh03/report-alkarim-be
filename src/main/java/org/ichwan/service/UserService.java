package org.ichwan.service;

public interface UserService<E, R> {

    public E register(R request);

    public E findByRegnumber(String regnumber);

    public boolean authenticate(String rawPassword, String passwordHash);
}
