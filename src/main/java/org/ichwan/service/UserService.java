package org.ichwan.service;

import org.ichwan.domain.User;

import java.util.List;

public interface UserService<E, R> {

    void register(E entity);

    void update(E entity, Long id);

    R findByRegnumber(String regnumber);

    R finById(Long id);

    E findEntityById(Long id);

    List<R> findByClsroomAndRoles(String classroom, String roles);

    List<R> findByRoles(String roles);

    boolean authenticate(String rawPassword, String passwordHash);

    void deleteUser(Long id);
}
