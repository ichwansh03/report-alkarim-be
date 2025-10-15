package org.ichwan.service;

import org.ichwan.domain.User;

import java.util.List;

public interface UserService<E> {

    void register(E entity);

    void update(E entity, Long id);

    E findByRegnumber(String regnumber);

    E finById(Long id);

    List<User> findByClsroomAndRoles(String classroom, String roles);

    List<User> findByRoles(String roles);

    boolean authenticate(String rawPassword, String passwordHash);
}
