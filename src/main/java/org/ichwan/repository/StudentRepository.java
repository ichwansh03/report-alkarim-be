package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.Student;

@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {
}
