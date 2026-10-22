package org.example;

import java.time.LocalDateTime;

/**
 * a class for all types of questions
 */

public abstract class Question<T> implements Gradable{
    private String text;
    private String author;
    private LocalDateTime timeOfAddition;
    private int difficulty;
    private double maxScore;
    protected T rightAnswer;

    protected Question (String text, String author, LocalDateTime timeOfAddition, int difficulty, double maxScore,
                        T rightAnswer) {
        this.text = text;
        this.author = author;
        this.timeOfAddition = timeOfAddition;
        this.difficulty = difficulty;
        this.maxScore = maxScore;
        this.rightAnswer = rightAnswer;
    }

    public T getRightAnswer() {
        return this.rightAnswer;
    }

    public String getText(){
        return this.text;
    }

    public String getAuthor(){
        return author;
    }

    public LocalDateTime getTimeOfAddition(){
        return timeOfAddition;
    }

    public int getDifficulty(){
        return difficulty;
    }

    public double getMaxScore(){
        return maxScore;
    }

    public abstract Enum<Correctness> checkAnswer(T input);
}
