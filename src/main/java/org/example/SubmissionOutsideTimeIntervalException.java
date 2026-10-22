package org.example;

import java.time.LocalDateTime;

/**
 * an exception class
 * which contains information about the timestamp at which it occurred and a toString method
 * for returning details about the exception.
 */

public class SubmissionOutsideTimeIntervalException extends Exception {
    private static final long serialVersionUID = 1L;

    private final LocalDateTime timestamp;

    public SubmissionOutsideTimeIntervalException(LocalDateTime timestamp) {
        super("Submission outside of time interval at " + timestamp.format(Platform.TIME_FORMAT));
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return "SubmissionOutsideTimeIntervalException: " + getMessage();
    }
}