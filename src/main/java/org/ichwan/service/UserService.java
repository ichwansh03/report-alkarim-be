package org.ichwan.service;

public interface UserService<E> {

    public E register(E entity);

    public E findByRegnumber(String regnumber);

    public boolean authenticate(String rawPassword, String passwordHash);
}
