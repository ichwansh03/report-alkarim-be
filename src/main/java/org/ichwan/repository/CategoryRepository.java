package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.Category;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {
}
