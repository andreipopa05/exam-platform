package examplatform;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 1, 9, 0);

    @Test
    void openEndedQuestionAcceptsLengthsWithinThirtyPercent() {
        OpenEndedQuestion question = new OpenEndedQuestion("text", "author", NOW, 1, 10, "last in first out");

        assertEquals(Correctness.correct, question.checkAnswer("last in first out"));
        // 21 letters, between 11 and 22, and it contains the right answer
        assertEquals(Correctness.partial, question.checkAnswer("the last in first out"));
        // 7 letters: too short
        assertEquals(Correctness.wrong, question.checkAnswer("last in"));
        assertEquals(Correctness.wrong, question.checkAnswer("something completely different"));

        assertEquals(10.0, question.score("last in first out"), 1e-9);
        assertEquals(7.0, question.score("the last in first out"), 1e-9);
        assertEquals(0.0, question.score("last in"), 1e-9);
    }

    @Test
    void fillInTheBlankQuestionAcceptsLengthsWithinTwoLetters() {
        FillInTheBlankQuestion question = new FillInTheBlankQuestion("text", "author", NOW, 1, 5, "FIFO");

        assertEquals(Correctness.correct, question.checkAnswer("FIFO"));
        assertEquals(Correctness.partial, question.checkAnswer("FIF"));
        assertEquals(Correctness.partial, question.checkAnswer("FIFOS"));
        // 1 letter: too short
        assertEquals(Correctness.wrong, question.checkAnswer("F"));
        assertEquals(Correctness.wrong, question.checkAnswer("LIFO"));

        assertEquals(2.5, question.score("FIF"), 1e-9);
    }

    @Test
    void multipleChoiceQuestionHasNoPartialCredit() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion("text", "author", NOW, 1, 10, ResponseOption.B);

        assertEquals(Correctness.correct, question.checkAnswer(ResponseOption.B));
        assertEquals(Correctness.wrong, question.checkAnswer(ResponseOption.A));

        assertEquals(10.0, question.score("B"), 1e-9);
        assertEquals(10.0, question.score("b"), 1e-9);
        assertEquals(0.0, question.score("D"), 1e-9);
    }
}