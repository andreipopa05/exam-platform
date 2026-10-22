package examplatform;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * is an exam
 */

public class Exam {
    // Oldest first; questions added at the same time are ordered by author
    private static final Comparator<Question<?>> HISTORY_ORDER = (a, b) -> {
        int byTime = a.getTimeOfAddition().compareTo(b.getTimeOfAddition());

        return byTime != 0 ? byTime : a.getAuthor().compareTo(b.getAuthor());
    };

    // Easiest first; questions with the same difficulty are ordered by text
    private static final Comparator<Question<?>> PRINT_ORDER = (a, b) -> {
        int byDifficulty = Integer.compare(a.getDifficulty(), b.getDifficulty());

        return byDifficulty != 0 ? byDifficulty : a.getText().compareTo(b.getText());
    };

    private final String name;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private final List<Question<?>> questions = new ArrayList<>();

    // The questions in the order in which the exam is printed; it is sorted only
    // when needed, and discarded when a question is added
    private List<Question<?>> printOrder;

    // Score of every student that took the exam; the TreeMap keeps the names sorted
    private final Map<String, Double> scores = new TreeMap<>();

    public Exam(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return this.name;
    }

    public void addQuestion(Question<?> qst) {
        questions.add(qst);
        printOrder = null;
    }

    private List<Question<?>> getPrintOrder() {
        if (printOrder == null) {
            printOrder = new ArrayList<>(questions);
            printOrder.sort(PRINT_ORDER);
        }

        return printOrder;
    }

    public String printQuestions() {
        List<Question<?>> sortedQuestions = new ArrayList<>(questions);
        sortedQuestions.sort(HISTORY_ORDER);

        StringBuilder details = new StringBuilder();

        for (Question<?> qst : sortedQuestions) {
            details.append(qst.getTimeOfAddition().format(Platform.TIME_FORMAT)).append(" | ")
                    .append(qst.getText()).append(" | ")
                    .append(qst.getRightAnswer()).append(" | ")
                    .append(qst.getDifficulty()).append(" | ")
                    .append(qst.getMaxScore()).append(" | ")
                    .append(qst.getAuthor()).append("\n");
        }

        return details.toString();
    }

    public String printExam() {
        StringBuilder details = new StringBuilder();

        for (Question<?> qst : getPrintOrder()) {
            details.append(qst.getMaxScore()).append(" | ")
                    .append(qst.getText()).append(" | ")
                    .append(qst.getDifficulty()).append(" | ")
                    .append(qst.getRightAnswer()).append("\n");
        }

        return details.toString();
    }

    public String printStudents() {
        StringBuilder details = new StringBuilder();

        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            details.append(String.format(Locale.US, "%s | %.2f\n", entry.getKey(), entry.getValue()));
        }

        return details.toString();
    }

    // The score of a student, or 0 if he/she did not take the exam
    public double getStudentScore(String studentName) {
        return scores.getOrDefault(studentName, 0.0);
    }

    // The answers follow the order in which the exam is printed. A submission outside
    // the time interval gets the score 0 and throws an exception
    public double submitStudentAnswers(String studentName, List<String> answers, LocalDateTime timestamp)
            throws SubmissionOutsideTimeIntervalException {
        if (timestamp.isBefore(startTime) || timestamp.isAfter(endTime)) {
            scores.put(studentName, 0.0);

            throw new SubmissionOutsideTimeIntervalException(timestamp);
        }

        List<Question<?>> orderedQuestions = getPrintOrder();
        double finalScore = 0.0;

        for (int i = 0; i < orderedQuestions.size(); i++) {
            finalScore += orderedQuestions.get(i).score(answers.get(i));
        }

        scores.put(studentName, finalScore);

        return finalScore;
    }
}