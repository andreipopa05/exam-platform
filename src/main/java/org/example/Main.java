package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * read/parse commands
 */

public class Main {
    public static void main(String[] args) throws IOException {
        String filename = "src/main/resources/" + args[0] + "/input.in";
        String filepath = "src/main/resources/" + args[0] + "/";

        Platform moodle = new Platform();

        try {
            BufferedReader breader = new BufferedReader(new FileReader(filename));

            String line = breader.readLine();

            if (line == null) {
                return;
            }

            while (line != null) {
                String[] command = line.split(" \\| ");

                if (command[1].equals("create_exam")) {
                    LocalDateTime start = getTime(command[3]);
                    LocalDateTime stop = getTime(command[4]);

                    moodle.createExam(command[2], start, stop);
                } else if (command[1].equals("add_question")) {
                    LocalDateTime timeOfAddition = getTime(command[0]);
                    int difficulty = Integer.parseInt(command[5]);
                    double maxScore = Double.parseDouble(command[6]);

                    moodle.addQuestion(command[2], command[3], command[7], command[4], timeOfAddition, difficulty,
                            maxScore, command[8]);
                } else if (command[1].equals("list_questions_history")) {
                    moodle.listQuestionsHistory(command[2], command[0], filepath);
                } else if (command[1].equals("print_exam")) {
                    moodle.printExam(command[2],command[0], filepath);
                } else if (command[1].equals("register_student")) {
                    moodle.registerStudent(command[2], command[3]);
                } else if (command[1].equals("submit_exam")) {
                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(command).subList(4, command.length));
                    LocalDateTime timestamp = getTime(command[0]);

                    moodle.submitExam(command[2], command[3], answers, timestamp, filepath, command[0]);
                } else if (command[1].equals("show_student_score")) {
                    moodle.showStudentScore(command[2], command[3], command[0], filepath);
                } else if (command[1].equals("generate_report")) {
                    moodle.generateExamReport(command[2], command[0],filepath);
                } else if (command[1].equals("exit")) {
                    break;
                }

                line = breader.readLine();
            }

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        System.out.println(args[0]);
    }

    private static LocalDateTime getTime(String command) {
        command = command.trim();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");

        return LocalDateTime.parse(command, formatter);
    }
}

