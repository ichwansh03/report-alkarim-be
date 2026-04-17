package org.ichwan.service;

import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.dto.response.ClassRoomResponse;

public interface ClassRoomService extends BaseService<ClassRoomRequest, ClassRoomResponse> {

    ClassRoomResponse getClassRoomByTeacherName(String teacherName);

}
