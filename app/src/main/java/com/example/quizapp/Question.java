package com.example.quizapp;

public class Question {
    private String question;
    private String answer;
    public Question (String Q, String A){
        this.question = Q;
        this.answer = A;
    }
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}

