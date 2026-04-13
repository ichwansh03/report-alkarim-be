package org.ichwan.dto.response;

import org.ichwan.domain.ClassRoom;
import org.ichwan.util.AnswerType;

public class QuestionResponse {

    private String question;
    private AnswerType options;
    private ClassRoom classRoom;

    public QuestionResponse() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public AnswerType getOptions() {
        return options;
    }

    public void setOptions(AnswerType options) {
        this.options = options;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }
}
