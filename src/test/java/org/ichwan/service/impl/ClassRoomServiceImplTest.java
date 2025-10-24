package org.ichwan.service.impl;

import org.ichwan.domain.ClassRoom;
import org.ichwan.repository.ClassRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClassRoomServiceImplTest {

    @Mock
    ClassRoomRepository classRoomRepository;

    @InjectMocks
    ClassRoomServiceImpl classRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByTeacherName() {
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName("Math 101");
        classRoom.setTeacherName("John Doe");
        classRoomRepository.persist(classRoom);
        when(classRoomRepository.findByTeacherName("John Doe")).thenReturn(classRoom);
        classRoomService.getClassRoomByTeacherName("John Doe");
        assertNotNull(classRoomService.getClassRoomByTeacherName("John Doe"));
    }

    @Test
    void testGetAllClassRooms() {
        classRoomService.getAllClassRooms();
        verify(classRoomRepository, times(1)).listAll();
    }
}
