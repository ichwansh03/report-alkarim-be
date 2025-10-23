package org.ichwan.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.ClassRoom;
import org.ichwan.service.ClassRoomService;

import java.util.List;

@ApplicationScoped
public class ClassRoomServiceImpl implements ClassRoomService<ClassRoom> {
    @Override
    public ClassRoom createClassRoom(ClassRoom classRoom) {
        return null;
    }

    @Override
    public ClassRoom getClassRoomById(Long id) {
        return null;
    }

    @Override
    public void updateClassRoom(ClassRoom classRoom, Long id) {

    }

    @Override
    public void deleteClassRoom(Long id) {

    }

    @Override
    public List<ClassRoom> getAllClassRooms() {
        return List.of();
    }
}
