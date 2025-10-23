package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.ClassRoom;

@ApplicationScoped
public class ClassRoomRepository implements PanacheRepository<ClassRoom> {
}
