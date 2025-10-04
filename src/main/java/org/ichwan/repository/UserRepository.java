package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User findByRegnumber(String regnumber) {
        return find("regnumber", regnumber).firstResult();
    }
}
