package org.ichwan.service;

import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.dto.response.ClassRoomResponse;

import java.util.List;

public interface ClassRoomService extends BaseService<ClassRoomResponse> {

    void createClassRoom(ClassRoomRequest classRoom);

    ClassRoomResponse getClassRoomByTeacherName(String teacherName);

    void updateClassRoom(ClassRoomRequest classRoom, Long id);

    void deleteClassRoom(Long id);

}
