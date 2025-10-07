package org.ichwan.service;

public interface UserService<E> {

    E register(E entity);

    E update(E entity, Long id);

    E findByRegnumber(String regnumber);

    E finById(Long id);

    boolean authenticate(String rawPassword, String passwordHash);
}
