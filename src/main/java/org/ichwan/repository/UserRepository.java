package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.User;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByRegnumber(String regnumber) {
        return find("regnumber", regnumber).firstResult();
    }

    public List<User> findByClsroomAndRoles(String classroom, String roles) {
        return find("clsroom = ?1 and roles = ?2", classroom, roles).list();
    }
}
