package examplatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * interprets commands and manages main objects
 */

public class Platform {
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");

    private final Path outputDirectory;
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, Exam> exams = new HashMap<>();

    public Platform(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void createExam(String name, LocalDateTime startTime, LocalDateTime endTime) {
        exams.putIfAbsent(name, new Exam(name, startTime, endTime));
    }

    public void addQuestion(String type, String examName, String text, String author, LocalDateTime timeOfAddition,
                            int difficulty, double maxScore, String rightAnswer) {
        Exam exam = exams.get(examName);

        if (exam == null) {
            return;
        }

        Question<?> question = switch (type) {
            case "open_ended" -> new OpenEndedQuestion(text, author, timeOfAddition, difficulty, maxScore,
                    rightAnswer);
            case "fill_in_the_blank" -> new FillInTheBlankQuestion(text, author, timeOfAddition, difficulty,
                    maxScore, rightAnswer);
            case "multiple_choice" -> new MultipleChoiceQuestion(text, author, timeOfAddition, difficulty,
                    maxScore, ResponseOption.valueOf(rightAnswer));
            default -> null;
        };

        if (question != null) {
            exam.addQuestion(question);
        }
    }

    public void listQuestionsHistory(String examName, LocalDateTime timestamp) throws IOException {
        String fileName = "questions_history_" + examName + "_" + timestamp.format(TIME_FORMAT) + ".out";

        writeFile(fileName, exams.get(examName).printQuestions());
    }

    public void printExam(String examName, LocalDateTime timestamp) throws IOException {
        String fileName = "print_exam_" + examName + "_" + timestamp.format(TIME_FORMAT) + ".out";

        writeFile(fileName, exams.get(examName).printExam());
    }

    public void registerStudent(String name, String group) {
        students.putIfAbsent(name, new Student(name, group));
    }

    public void showStudentScore(String studentName, String examName, LocalDateTime timestamp) throws IOException {
        Exam exam = exams.get(examName);

        if (!students.containsKey(studentName) || exam == null) {
            return;
        }

        String details = String.format(Locale.US, "%s | The score of %s for exam %s is %.2f",
                timestamp.format(TIME_FORMAT), studentName, examName, exam.getStudentScore(studentName));

        appendToConsole(details);
    }

    public void generateExamReport(String examName, LocalDateTime timestamp) throws IOException {
        String fileName = "exam_report_" + examName + "_" + timestamp.format(TIME_FORMAT) + ".out";

        writeFile(fileName, exams.get(examName).printStudents());
    }

    public void submitExam(String examName, String studentName, List<String> answers, LocalDateTime timestamp)
            throws IOException {
        Student student = students.get(studentName);
        Exam exam = exams.get(examName);

        try {
            double score = exam.submitStudentAnswers(studentName, answers, timestamp);

            student.addResult(examName, score);
        } catch (SubmissionOutsideTimeIntervalException exception) {
            student.addResult(examName, 0.0);

            appendToConsole(exception.getTimestamp().format(TIME_FORMAT)
                    + " | Submission outside of time interval for student " + student.getName());
        }
    }

    // Writes a file in the output directory, replacing it if it already exists
    private void writeFile(String fileName, String content) throws IOException {
        Files.writeString(outputDirectory.resolve(fileName), content);
    }

    // Adds a line at the end of console.out
    private void appendToConsole(String line) throws IOException {
        Files.writeString(outputDirectory.resolve("console.out"), line + "\n",
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}