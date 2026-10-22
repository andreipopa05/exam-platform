package org.example;

import java.time.LocalDateTime;

public class OpenEndedQuestion extends Question<String> {
    protected OpenEndedQuestion(String text, String author, LocalDateTime timeOfAddition, int difficulty,
                                double maxScore, String rightAnswer) {
        super(text, author, timeOfAddition, difficulty, maxScore, rightAnswer);
    }

    @Override
    public double scorePercentage(Correctness correct) {
        if (correct == Correctness.correct) {
            return 1;
        }

        if (correct == Correctness.partial) {
            return 0.7;
        }

        return 0;
    }

    @Override
    protected String parseAnswer(String answer) {
        return answer;
    }

    @Override
    public Correctness checkAnswer(String input) {
        if (input.equals(this.rightAnswer)) {
            return Correctness.correct;
        }

        int lowBound = (int) (0.7 * rightAnswer.length());
        int highBound = (int) (1.3 * rightAnswer.length());

        if ((input.length() >= lowBound && input.length() <= highBound) &&
                (input.contains(this.rightAnswer) || this.rightAnswer.contains(input))) {
            return Correctness.partial;
        }

        return Correctness.wrong;
    }
}