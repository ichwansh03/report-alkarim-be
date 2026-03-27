package org.ichwan.service;

import org.ichwan.domain.User;
import org.ichwan.dto.UserRequest;
import org.ichwan.dto.UserResponse;

import java.util.List;

public interface UserService extends BaseService<UserResponse> {

    void update(UserRequest entity, Long id);

    UserResponse findByRegnumber(String regnumber);

    User findEntityById(Long id);

    List<UserResponse> findByClsroomAndRoles(String classroom, String roles);

    List<UserResponse> findByRoles(String roles);

    void deleteUser(Long id);
}
