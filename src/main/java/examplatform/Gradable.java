package examplatform;

/**
 * defines the behavior of entities that can be graded
 */

public interface Gradable {
    double scorePercentage(Correctness correct);
}