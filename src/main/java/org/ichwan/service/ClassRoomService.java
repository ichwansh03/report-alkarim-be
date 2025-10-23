package org.ichwan.service;

import java.util.List;

public interface ClassRoomService<E> {

    E createClassRoom(E classRoom);

    E getClassRoomById(Long id);

    void updateClassRoom(E classRoom, Long id);

    void deleteClassRoom(Long id);

    List<E> getAllClassRooms();
}
