package com.example.synomatch.quiz;

import java.util.List;

public class QuizItem {
    public final String baseWord;
    public final String correctAnswer;
    public final List<String> options;

    public QuizItem(String baseWord, String correctAnswer, List<String> options) {
        this.baseWord = baseWord;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }
}
