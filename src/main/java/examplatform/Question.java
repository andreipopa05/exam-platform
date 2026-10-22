package examplatform;

import java.time.LocalDateTime;

/**
 * a class for all types of questions
 */

public abstract class Question<T> implements Gradable {
    private final String text;
    private final String author;
    private final LocalDateTime timeOfAddition;
    private final int difficulty;
    private final double maxScore;
    protected final T rightAnswer;

    protected Question(String text, String author, LocalDateTime timeOfAddition, int difficulty, double maxScore,
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

    public String getText() {
        return this.text;
    }

    public String getAuthor() {
        return this.author;
    }

    public LocalDateTime getTimeOfAddition() {
        return this.timeOfAddition;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public double getMaxScore() {
        return this.maxScore;
    }

    // Converts an answer, as it appears in the input, to the type used by checkAnswer
    protected abstract T parseAnswer(String answer);

    public abstract Correctness checkAnswer(T input);

    // Score obtained for an answer given as text: the maximum score times the
    // percentage that the degree of correctness is worth for this type of question
    public double score(String answer) {
        return this.maxScore * scorePercentage(checkAnswer(parseAnswer(answer)));
    }
}