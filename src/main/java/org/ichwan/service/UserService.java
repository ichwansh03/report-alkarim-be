package org.ichwan.service;

public interface UserService<E> {

    void register(E entity);

    void update(E entity, Long id);

    E findByRegnumber(String regnumber);

    E finById(Long id);

    boolean authenticate(String rawPassword, String passwordHash);
}
