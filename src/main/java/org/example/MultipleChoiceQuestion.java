package org.example;

import java.time.LocalDateTime;

public class MultipleChoiceQuestion extends Question<ResponseOption> {
    protected MultipleChoiceQuestion(String text, String author, LocalDateTime timeOfAddition, int difficulty,
                                     double maxScore, ResponseOption rightAnswer) {
        super(text, author, timeOfAddition, difficulty, maxScore, rightAnswer);
    }

    @Override
    public double scorePercentage(Correctness correct) {
        if (correct == Correctness.correct) {
            return 1;
        }

        return 0;
    }

    @Override
    protected ResponseOption parseAnswer(String answer) {
        return ResponseOption.valueOf(answer.trim().toUpperCase());
    }

    @Override
    public Correctness checkAnswer(ResponseOption input) {
        if (input == this.rightAnswer) {
            return Correctness.correct;
        }

        return Correctness.wrong;
    }
}