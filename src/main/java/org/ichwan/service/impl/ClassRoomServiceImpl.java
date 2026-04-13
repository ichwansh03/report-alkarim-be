package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.ClassRoom;
import org.ichwan.domain.Question;
import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.dto.response.ClassRoomResponse;
import org.ichwan.dto.response.PageResponse;
import org.ichwan.dto.response.QuestionResponse;
import org.ichwan.exceptions.ConflictException;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.ClassRoomRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ClassRoomService;
import org.ichwan.util.MapperConfig;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClassRoomServiceImpl implements ClassRoomService {

    @Inject
    private ClassRoomRepository repository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MapperConfig mapper;

    @Override
    @Transactional
    public void createClassRoom(ClassRoomRequest classRoom) {
        // Check if classroom name already exists
        boolean exists = repository.find("name", classRoom.name()).firstResultOptional().isPresent();
        if (exists) {
            throw new ConflictException("Classroom with name '" + classRoom.name() + "' already exists");
        }

        ClassRoom room = new ClassRoom();
        room.setName(classRoom.name());
        room.setTeacher(userRepository.findById(classRoom.teacherId()));
        room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.name(), "STUDENT").size());
        repository.persist(room);
    }

    @Override
    public ClassRoomResponse getClassRoomByTeacherName(String teacherName) {
        ClassRoom classRoom = Optional.ofNullable(repository.findByTeacherName(teacherName))
                .orElseThrow(() -> new NotFoundException("Classroom with teacher name '" + teacherName + "' not found"));
        return mapper.map(classRoom, ClassRoomResponse.class);
    }

    @Override
    @Transactional
    public void updateClassRoom(ClassRoomRequest classRoom, Long id) {
        ClassRoom room = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Classroom with id " + id + " not found"));

        // Check if the new name conflicts with another existing classroom
        repository.find("name = ?1 and id != ?2", classRoom.name(), id)
                .firstResultOptional()
                .ifPresent(r -> {
                    throw new ConflictException("Classroom with name '" + classRoom.name() + "' already exists");
                });

        room.setName(classRoom.name());
        room.setTeacher(userRepository.findById(classRoom.teacherId()));
        room.setStudentCount(userRepository.findByClsroomAndRoles(classRoom.name(), "STUDENT").size());
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

    @Override
    public ClassRoomResponse findById(Long id) {
        ClassRoom classRoom = Optional.ofNullable(repository.findById(id))
                .orElseThrow(() -> new NotFoundException("Classroom with id '" + id + "' not found"));
        return mapper.map(classRoom, ClassRoomResponse.class);
    }

    @Override
    public PageResponse<ClassRoomResponse> getAll(int page, int size) {
        PanacheQuery<ClassRoom> query = repository.findAll();
        query.page(Page.of(page, size));

        List<ClassRoom> classRooms = query.list();
        if (classRooms.isEmpty()) {
            throw new NotFoundException("No users found");
        }

        long totalItems = query.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<ClassRoomResponse> data = mapper.mapList(classRooms, ClassRoomResponse.class);

        return new PageResponse<>(data, page, size, totalItems, totalPages);
    }
}
