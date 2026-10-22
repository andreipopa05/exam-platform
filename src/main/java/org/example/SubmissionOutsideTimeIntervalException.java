package org.example;

/**
 * an exception class
 * which will contain information about the timestamp at which it occurred and a toString method
 * for returning details about the exception.
 */

public class SubmissionOutsideTimeIntervalException extends RuntimeException {
    public SubmissionOutsideTimeIntervalException(String message) {
        super(message);
    }
}
