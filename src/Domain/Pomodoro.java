package Domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Wrapper class for the database abstraction Pomodoro.
 * @author Esben Christensen
 */
public class Pomodoro {
    /**
     * Duration of the work period of the pomodoro
     */
    Duration workDuration;
    /**
     * Duration of the break period of the pomodoro
     */
    Duration breakDuration;
    /**
     * Point in time when the pomodoro starts
     */
    LocalDateTime start;
    /**
     * Point in time when the pomodoro ends
     */
    LocalDateTime end;

    /**
     * Constructor of Pomodoro object
     * @param workDuration the work duration of the pomodoro
     * @param breakDuration the break duration of the pomodoro
     * @param start the point in time when the pomodoro starts
     * @param end the point in time when the pomodoro ends
     */
    public Pomodoro(Duration workDuration, Duration breakDuration, LocalDateTime start, LocalDateTime end) {
        this.workDuration = workDuration;
        this.breakDuration = breakDuration;
        this.start = start;
        this.end = end;
    }

    /**
     * Get start time
     * @return start time
     */
    public LocalDateTime getStart() { return start; }

    /**
     * Get end time
     * @return end time
     */
    public LocalDateTime getEnd() { return end; }

    /**
     * Get duration of break period
     * @return break period duration
     */
    public Duration getBreakDuration() { return breakDuration; }
    /**
     * Get duration of work period
     * @return work period duration
     */
    public Duration getWorkDuration() { return workDuration; }
}
