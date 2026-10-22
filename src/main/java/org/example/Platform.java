package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * interprets commands and manages main objects
 */

public class Platform {
    private final Map <String, Student> students;
    private final Map <String, Exam> exams;

    public Platform() {
        students = new HashMap<>();
        exams = new HashMap<>();
    }

    public void createExam(String name, LocalDateTime startTime, LocalDateTime endTime) {
        if (exams.get(name) == null) {
            Exam exam = new Exam(name, startTime, endTime);
            exams.put(name, exam);
        }
    }

    public void addQuestion(String type, String examName, String text, String author, LocalDateTime timeOfAddition, int difficulty, double maxScore, String rightAnswer) {
        if (exams.get(examName) == null) {
            return;
        }

        if (type.equals("open_ended")) {
            OpenEndedQuestion qst = new OpenEndedQuestion(text, author, timeOfAddition, difficulty, maxScore,
                    rightAnswer);

            exams.get(examName).addQuestion(qst);
        } else if (type.equals("fill_in_the_blank")) {
            FillInTheBlankQuestion qst = new FillInTheBlankQuestion(text, author, timeOfAddition, difficulty, maxScore,
                    rightAnswer);

            exams.get(examName).addQuestion(qst);
        } else if (type.equals("multiple_choice")) {
            ResponseOption resp = null;

            switch (rightAnswer) {
                case "A" -> resp = ResponseOption.A;
                case "B" -> resp = ResponseOption.B;
                case "C" -> resp = ResponseOption.C;
                case "D" -> resp = ResponseOption.D;
            }

            MultipleChoiceQuestion qst = new MultipleChoiceQuestion(text, author, timeOfAddition, difficulty, maxScore, resp);

            exams.get(examName).addQuestion(qst);
        }
    }

    public void listQuestionsHistory(String examName, String timestamp, String filepath) throws IOException {
        String toPrint = exams.get(examName).printQuestions();

        String filename = filepath +  "questions_history_" + examName + "_" + timestamp + ".out";

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        bufferedWriter.write(toPrint);
        bufferedWriter.close();
    }

    public void printExam(String examName, String timestamp, String filepath) throws IOException {
        String toPrint = exams.get(examName).printExam();

        String filename = filepath + "print_exam_" + examName + "_" + timestamp + ".out";

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
        bufferedWriter.write(toPrint);
        bufferedWriter.close();
    }

    public void registerStudent(String name, String group) {
        if (students.get(name) == null) {
            Student student = new Student(name, group);

            students.put(name, student);
        }
    }

    public void showStudentScore(String studentName, String examName, String timestamp, String filepath)
            throws IOException {
        if (students.get(studentName) == null || exams.get(examName) == null) {
            return;
        }

        Double studentScore = exams.get(examName).getStudentScore(studentName);

        String details = String.format("%s | The score of %s for exam %s is %.2f", timestamp, studentName, examName, studentScore);

        String filename = filepath + "console.out";

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
        bufferedWriter.write(details);
        bufferedWriter.newLine();
        bufferedWriter.close();
    }

    public void generateExamReport(String examName, String timestamp,String filepath) throws IOException {
        exams.get(examName).printStudents(examName, timestamp,filepath);
    }

    public void submitExam(String examName, String studentName, ArrayList<String> answers, LocalDateTime timestamp,
                           String filepath, String time) throws IOException {
        Student student = students.get(studentName);
        Exam exam = exams.get(examName);

        try {
            exam.submitStudentAnswers(student, answers, timestamp);
        } catch (SubmissionOutsideTimeIntervalException exception) {
            String filename = filepath + "console.out";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true));
            String details = time + " | Submission outside of time interval for student " + student.getName();

            bufferedWriter.write(details);
            bufferedWriter.newLine();
            bufferedWriter.close();

            exam.getStudents().put(student, 0.00);
            student.getExams().put(exam, 0.00);
        }
    }
}
