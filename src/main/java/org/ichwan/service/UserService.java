package org.ichwan.service;

import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.UserResponse;

import java.util.List;

public interface UserService extends BaseService<UserRequest, UserResponse> {

    UserResponse findByRegnumber(String regnumber);

    List<UserResponse> findByClsroomAndRoles(String classroom, String roles);

    List<UserResponse> findByRoles(String roles);

}
