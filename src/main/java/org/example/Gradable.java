package org.example;

/**
 * defines the behavior of entities that can be noted
 */

public interface Gradable {
    abstract double scorePercentage (Enum<Correctness> correct);
}
