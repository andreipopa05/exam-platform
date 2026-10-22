package examplatform;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * read/parse commands
 */

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: Main <input_file> <output_directory>");
            System.exit(1);
        }

        Path outputDirectory = Path.of(args[1]);

        // The output directory is created if needed, and console.out starts empty
        Files.createDirectories(outputDirectory);
        Files.deleteIfExists(outputDirectory.resolve("console.out"));

        Platform moodle = new Platform(outputDirectory);

        try (BufferedReader reader = Files.newBufferedReader(Path.of(args[0]))) {
            String line = reader.readLine();

            while (line != null) {
                if (!line.isBlank()) {
                    String[] command = line.split(" \\| ");

                    if (command[1].equals("exit")) {
                        break;
                    }

                    execute(moodle, command);
                }

                line = reader.readLine();
            }
        }
    }

    private static void execute(Platform moodle, String[] command) throws IOException {
        switch (command[1]) {
            case "create_exam" -> moodle.createExam(command[2], getTime(command[3]), getTime(command[4]));
            case "add_question" -> moodle.addQuestion(command[2], command[3], command[7], command[4],
                    getTime(command[0]), Integer.parseInt(command[5]), Double.parseDouble(command[6]), command[8]);
            case "list_questions_history" -> moodle.listQuestionsHistory(command[2], getTime(command[0]));
            case "print_exam" -> moodle.printExam(command[2], getTime(command[0]));
            case "register_student" -> moodle.registerStudent(command[2], command[3]);
            case "submit_exam" -> {
                List<String> answers = Arrays.asList(command).subList(4, command.length);

                moodle.submitExam(command[2], command[3], answers, getTime(command[0]));
            }
            case "show_student_score" -> moodle.showStudentScore(command[2], command[3], getTime(command[0]));
            case "generate_report" -> moodle.generateExamReport(command[2], getTime(command[0]));
            default -> { }
        }
    }

    private static LocalDateTime getTime(String command) {
        return LocalDateTime.parse(command.trim(), Platform.TIME_FORMAT);
    }
}