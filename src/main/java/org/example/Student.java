package org.example;

import java.util.HashMap;
import java.util.Map;

/**
 * models a student and his/her results
 */

public class Student {
    private final String name;
    private final String group;

    private final Map<Exam, Double> exams;

    public Student(String name, String group) {
        this.name = name;
        this.group = group;
        this.exams = new HashMap<>();
    }

    public Map<Exam, Double> getExams() {
        return this.exams;
    }

    public String getName() {
        return this.name;
    }
}
