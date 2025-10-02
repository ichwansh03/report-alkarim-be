package org.ichwan.service;

public interface UserService<E> {

    public E register(E entity);

    public E findByEmail(String email);

    public boolean authenticate(String rawPassword, String passwordHash);
}
