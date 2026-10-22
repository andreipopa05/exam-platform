package org.example;

import java.time.LocalDateTime;

public class FillInTheBlankQuestion extends Question<String>{
    protected FillInTheBlankQuestion(String text, String author, LocalDateTime timeOfAddition, int difficulty,
                                     double maxScore, String rightAnswer) {
        super(text, author, timeOfAddition, difficulty, maxScore, rightAnswer);
    }

    @Override
    public double scorePercentage(Enum<Correctness> correct) {
        if (correct == Correctness.correct) {
            return 1;
        }

        if (correct == Correctness.partial) {
            return 0.5;
        }

        return 0;
    }

    @Override
    public Enum<Correctness> checkAnswer(String input) {
        if (input.equals(this.rightAnswer)) {
            return Correctness.correct;
        }

        int lowBound = rightAnswer.length() - 2;
        int highBound = rightAnswer.length() + 2;

        if ((input.length() >= lowBound && input.length() <= highBound) &&
                (input.contains(this.rightAnswer) || this.rightAnswer.contains(input))) {
            return Correctness.partial;
        }

        return Correctness.wrong;
    }
}
