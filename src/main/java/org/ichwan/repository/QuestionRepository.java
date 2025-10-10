package org.ichwan.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ichwan.domain.Question;

import java.util.List;

@ApplicationScoped
public class QuestionRepository implements PanacheRepository<Question> {

    public List<Question> getQuestionByTarget(String target) {
        return find("target", target).list();
    }
}
