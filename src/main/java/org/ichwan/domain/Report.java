package org.ichwan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String content;
    private Boolean action;
    private String marked;
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

    public Boolean getAction() {
        return action;
    }

    public void setAction(Boolean action) {
        this.action = action;
    }

    public String getMarked() {
        return marked;
    }

    public void setMarked(String marked) {
        this.marked = marked;
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
                "id=" + id +
                ", category='" + category + '\'' +
                ", content='" + content + '\'' +
                ", action=" + action +
                ", marked='" + marked + '\'' +
                ", user=" + user +
                '}';
    }
}
