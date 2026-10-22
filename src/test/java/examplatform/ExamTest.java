package examplatform;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExamTest {
    private static final LocalDateTime START = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime END = LocalDateTime.of(2025, 1, 1, 12, 0);

    private final Exam exam = new Exam("Exam", START, END);

    @Test
    void submissionOutsideTheIntervalThrowsAndScoresZero() {
        SubmissionOutsideTimeIntervalException exception = assertThrows(
                SubmissionOutsideTimeIntervalException.class,
                () -> exam.submitStudentAnswers("Ana", List.of(), END.plusMinutes(1)));

        assertEquals(END.plusMinutes(1), exception.getTimestamp());
        assertEquals(0.0, exam.getStudentScore("Ana"), 1e-9);
        assertThrows(SubmissionOutsideTimeIntervalException.class,
                () -> exam.submitStudentAnswers("Ana", List.of(), START.minusMinutes(1)));
    }

    @Test
    void submissionsExactlyAtTheLimitsAreAccepted() throws SubmissionOutsideTimeIntervalException {
        assertEquals(0.0, exam.submitStudentAnswers("Ana", List.of(), START), 1e-9);
        assertEquals(0.0, exam.submitStudentAnswers("Bogdan", List.of(), END), 1e-9);
    }

    @Test
    void answersFollowThePrintOrderEvenWhenQuestionsAreAddedLater() throws SubmissionOutsideTimeIntervalException {
        LocalDateTime added = START.minusHours(1);

        exam.addQuestion(new FillInTheBlankQuestion("b", "author", added, 2, 4, "yes"));
        exam.printExam();
        // Easier than the first one, so it has to be answered first
        exam.addQuestion(new OpenEndedQuestion("a", "author", added, 1, 10, "no"));

        assertEquals(14.0, exam.submitStudentAnswers("Ana", List.of("no", "yes"), START), 1e-9);
    }

    @Test
    void reportListsTheStudentsAlphabetically() throws SubmissionOutsideTimeIntervalException {
        exam.submitStudentAnswers("Zoe", List.of(), START);
        exam.submitStudentAnswers("Ana", List.of(), START);

        assertEquals("Ana | 0.00\nZoe | 0.00\n", exam.printStudents());
    }
}