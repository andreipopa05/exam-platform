package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * is an exam
 */

public class Exam {
    private final String name;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private List<Question<?>> questions;
    private Map<Student, Double> students;

    public Exam(String name, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        questions = new ArrayList<>();
        students = new HashMap<>();
    }

    public Map<Student, Double> getStudents() {
        return students;
    }

    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }


    public List<Question<?>> getQuestions() {
        return questions;
    }

    public void addQuestion(Question<?> qst) {
        questions.add(qst);
    }

    public String printQuestions() {
        List <Question<?>> printSortedQuestions = new ArrayList<>(questions);

        printSortedQuestions.sort(new MyComparatorQuestions());

        StringBuilder details = new StringBuilder();

        for (Question<?> qst : printSortedQuestions) {
            String time = formatTime(qst.getTimeOfAddition());

            details.append(time).append(" | ").append(qst.getText()).append(" | ").append(qst.getRightAnswer()).append(" | ").append(qst.getDifficulty()).append(" | ").append(qst.getMaxScore()).append(" | ").append(qst.getAuthor());
            details.append("\n");
        }

        return details.toString();
    }

    public String printExam() {
        List<Question<?>> printSortedExam = new ArrayList<>(questions);

        printSortedExam.sort(new MyComparatorExam());

        this.questions = printSortedExam;

        StringBuilder details = new StringBuilder();

        for (Question<?> qst : printSortedExam) {
            details.append(qst.getMaxScore()).append(" | ").append(qst.getText()).append(" | ").append(qst.getDifficulty()).append(" | ").append(qst.getRightAnswer());
            details.append("\n");
        }

        return details.toString();
    }

    public void printStudents(String examName, String timestamp,String filepath) throws IOException {
        String filename = filepath + "exam_report_" + examName + "_" + timestamp + ".out";

        BufferedWriter br = new BufferedWriter(new FileWriter(filename));

        TreeMap<Student, Double> sortedStudents = new TreeMap<>(new MyComparatorStudents());
        sortedStudents.putAll(students);

        for (Map.Entry<Student, Double> entry : sortedStudents.entrySet()) {
            Student std = entry.getKey();
            Double scr = entry.getValue();

            String details = String.format("%s | %.2f\n", std.getName(), scr);

            br.write(details);
        }

        br.close();
    }

    public Double getStudentScore (String name) {
        for (Map.Entry<Student, Double> entry : students.entrySet()) {
            if (entry.getKey().getName().equals(name)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public void submitStudentAnswers(Student student, ArrayList<String> answers, LocalDateTime timestamp)
            throws SubmissionOutsideTimeIntervalException, IOException {
            if (timestamp.isAfter(this.getEndTime()) || timestamp.isBefore(this.getStartTime())) {
                throw new SubmissionOutsideTimeIntervalException(timestamp.toString());
            }

        double finalScore = 0.00;

        for (int i = 0; i < this.getQuestions().size(); i++) {
            if (this.getQuestions().get(i) instanceof MultipleChoiceQuestion qst) {
                ResponseOption responseOption = null;

                switch (answers.get(i)) {
                    case "A" -> responseOption = ResponseOption.A;
                    case "B" -> responseOption = ResponseOption.B;
                    case "C" -> responseOption = ResponseOption.C;
                    case "D" -> responseOption = ResponseOption.D;
                }

                assert responseOption != null;
                double score = qst.getMaxScore() * qst.scorePercentage(qst.checkAnswer(responseOption));

                finalScore += score;
            } else if (this.getQuestions().get(i) instanceof OpenEndedQuestion qst) {
                double score = qst.getMaxScore() * qst.scorePercentage(qst.checkAnswer(answers.get(i)));

                finalScore += score;
            } else {
                FillInTheBlankQuestion qst = (FillInTheBlankQuestion) this.getQuestions().get(i);
                double score = qst.getMaxScore() * qst.scorePercentage(qst.checkAnswer(answers.get(i)));

                finalScore += score;
            }
        }

        this.getStudents().put(student, finalScore);
        student.getExams().put(this, finalScore);
    }

    private static String formatTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");

        return date.format(formatter);
    }
}

class MyComparatorQuestions implements Comparator<Question<?>> {
    @Override
    public int compare(Question o1, Question o2) {
        if (!o1.getTimeOfAddition().equals(o2.getTimeOfAddition())) {
            return o1.getTimeOfAddition().compareTo(o2.getTimeOfAddition());
        }

        return o1.getAuthor().compareTo(o2.getAuthor());
    }
}

class MyComparatorExam implements Comparator<Question<?>> {
    @Override
    public int compare(Question<?> o1, Question<?> o2) {
        if (o1.getDifficulty() != o2.getDifficulty()) {
            return Integer.compare(o1.getDifficulty(), o2.getDifficulty());
        }

        return o1.getText().compareTo(o2.getText());
    }
}

class MyComparatorStudents implements Comparator<Student> {
    @Override
    public int compare(Student o1, Student o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
