package org.example;

import java.time.LocalDateTime;

public class MultipleChoiceQuestion extends Question<ResponseOption>{
    protected MultipleChoiceQuestion(String text, String author, LocalDateTime timeOfAddition, int difficulty,
                                     double maxScore, ResponseOption rightAnswer) {
        super(text, author, timeOfAddition, difficulty, maxScore, rightAnswer);
    }

    @Override
    public double scorePercentage(Enum<Correctness> correct) {
        if (correct == Correctness.correct) {
            return 1;
        }

        return 0;
    }

    @Override
    public Enum<Correctness> checkAnswer(ResponseOption input) {
        if (input.equals(this.rightAnswer)) {
            return Correctness.correct;
        }

        return Correctness.wrong;
    }
}
