package Domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Wrapper class for the database abstraction Pomodoro.
 */
public class Pomodoro {
    Duration workDuration;
    Duration breakDuration;
    LocalDateTime start;
    LocalDateTime end;

    public Pomodoro(Duration workDuration, Duration breakDuration, LocalDateTime start, LocalDateTime end) {
        this.workDuration = workDuration;
        this.breakDuration = breakDuration;
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public Duration getBreakDuration() { return breakDuration; }
    public Duration getWorkDuration() { return workDuration; }
}
