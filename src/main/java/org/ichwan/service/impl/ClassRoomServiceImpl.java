package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ichwan.domain.ClassRoom;
import org.ichwan.repository.ClassRoomRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ClassRoomService;

import java.util.List;

@ApplicationScoped
public class ClassRoomServiceImpl implements ClassRoomService<ClassRoom> {

    @Inject
    private ClassRoomRepository repository;

    @Inject
    private UserRepository userRepository;

    @Override
    public ClassRoom createClassRoom(ClassRoom classRoom) {

        ClassRoom room = new ClassRoom();
        room.setName(classRoom.getName());
        room.setTeacherName(classRoom.getTeacherName());
        room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.getName(), "STUDENT").size());
        repository.persist(room);
        return room;
    }

    @Override
    public ClassRoom getClassRoomByTeacherName(String teacherName) {
        return repository.findByTeacherName(teacherName);
    }


    @Override
    public void updateClassRoom(ClassRoom classRoom, Long id) {
        if (repository.findById(id) != null) {
            ClassRoom room = repository.findById(id);
            room.setName(classRoom.getName());
            room.setTeacherName(classRoom.getTeacherName());
            room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.getName(), "STUDENT").size());
            repository.persist(room);
        } else {
            throw new IllegalArgumentException("classroom not found");
        }
    }

    @CacheInvalidate(cacheName = "classrooms")
    @Override
    public void deleteClassRoom(Long id) {
        repository.deleteById(id);
    }

    @CacheResult(cacheName = "classrooms", lockTimeout = 3000)
    @Override
    public List<ClassRoom> getAllClassRooms() {
        return repository.listAll();
    }
}
