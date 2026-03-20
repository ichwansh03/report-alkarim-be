package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.ClassRoom;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.ClassRoomRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ClassRoomService;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClassRoomServiceImpl implements ClassRoomService<ClassRoom> {

    @Inject
    private ClassRoomRepository repository;

    @Inject
    private UserRepository userRepository;

    @Override
    @Transactional
    public ClassRoom createClassRoom(ClassRoom classRoom) {
        // Check if classroom name already exists
        boolean exists = repository.find("name", classRoom.getName()).firstResultOptional().isPresent();
        if (exists) {
            throw new ConflictException("Classroom with name '" + classRoom.getName() + "' already exists");
        }

        ClassRoom room = new ClassRoom();
        room.setName(classRoom.getName());
        room.setTeacherName(classRoom.getTeacherName());
        room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.getName(), "STUDENT").size());
        repository.persist(room);
        return room;
    }

    @Override
    public ClassRoom getClassRoomByTeacherName(String teacherName) {
        return Optional.ofNullable(repository.findByTeacherName(teacherName))
                .orElseThrow(() -> new NotFoundException("Classroom with teacher name '" + teacherName + "' not found"));
    }

    @Override
    @Transactional
    public void updateClassRoom(ClassRoom classRoom, Long id) {
        ClassRoom room = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Classroom with id " + id + " not found"));

        // Check if the new name conflicts with another existing classroom
        repository.find("name = ?1 and id != ?2", classRoom.getName(), id)
                .firstResultOptional()
                .ifPresent(r -> {
                    throw new ConflictException("Classroom with name '" + classRoom.getName() + "' already exists");
                });

        room.setName(classRoom.getName());
        room.setTeacherName(classRoom.getTeacherName());
        room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.getName(), "STUDENT").size());
        repository.persist(room);
    }

    @CacheInvalidate(cacheName = "classrooms")
    @Override
    @Transactional
    public void deleteClassRoom(Long id) {
        repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Classroom with id " + id + " not found"));

        repository.deleteById(id);
    }

    @CacheResult(cacheName = "classrooms", lockTimeout = 3000)
    @Override
    public List<ClassRoom> getAllClassRooms() {
        List<ClassRoom> classRooms = repository.listAll();
        if (classRooms.isEmpty()) {
            throw new NotFoundException("No classrooms found");
        }
        return classRooms;
    }
}
