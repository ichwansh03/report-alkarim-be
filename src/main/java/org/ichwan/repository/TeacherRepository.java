package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.Teacher;

@ApplicationScoped
public class TeacherRepository implements PanacheRepository<Teacher> {
}
