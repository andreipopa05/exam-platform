package examplatform;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * models a student and his/her results
 */

public class Student {
    private final String name;
    private final String group;

    // Score obtained at each exam, found by the name of the exam
    private final Map<String, Double> examScores;

    public Student(String name, String group) {
        this.name = name;
        this.group = group;
        this.examScores = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public String getGroup() {
        return this.group;
    }

    public void addResult(String examName, double score) {
        examScores.put(examName, score);
    }

    public Map<String, Double> getExamScores() {
        return Collections.unmodifiableMap(examScores);
    }
}