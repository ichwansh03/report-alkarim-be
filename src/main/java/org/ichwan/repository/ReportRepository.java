package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.Report;

import java.util.List;

@ApplicationScoped
public class ReportRepository implements PanacheRepository<Report> {

    public List<Report> findByUserRegnumber(String regnumber) {
        return find("user.regnumber", regnumber).list();
    }

    public List<Report> findByUserName(String name) {
        return find("user.name", name).list();
    }

    public List<Report> findByCategory(String category) {
        return find("category", category).list();
    }
}
