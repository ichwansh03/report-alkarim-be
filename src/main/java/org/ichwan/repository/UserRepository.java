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

    public List<User> findByClsroom(String classroom) {
        return find("clsroom", classroom).list();
    }
}
