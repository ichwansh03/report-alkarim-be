package org.ichwan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String content;
    private String answer;
    private String score;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Report() {
    }

    public Long getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Report{" +
                "category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", answer='" + answer + '\'' +
                ", score='" + score + '\'' +
                ", user=" + user +
                '}';
    }
}
