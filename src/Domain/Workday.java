package Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Wrapper class for the database abstraction Workday.
 * @author Esben Christensen
 */
public class Workday {
    /**
     * primary key of the workday in the database
     */
    int id;
    /**
     * Point in time when the workday starts
     */
    LocalDateTime start;
    /**
     * Point in time when the workday ends
     */
    LocalDateTime end;
    /**
     * List of pomodoros in the workday
     */
    ArrayList<Pomodoro> pomodoros = new ArrayList<Pomodoro>();
    /**
     * When the workday was last updated.
     */
    String updated;

    /**
     * Workday constructor
     * @param id ID of workday
     * @param start Start time of workday
     * @param end End time of workday
     * @param updated Last updated time of workday
     */
    public Workday(int id, LocalDateTime start, LocalDateTime end, String updated) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.updated = updated;
    }

    /**
     * Sets the ID of the workday
     * @param id the ID to set
     */
    public void setID(int id) { this.id = id; }

    /**
     * Gets the ID of the workday
     * @return the ID
     */
    public int getId() { return id; }

    /**
     * Sets the start of the workday
     * @param time start time of workday
     */
    public void setStart(LocalDateTime time) { this.start = time; }

    /**
     * Gets the start time of the workday
     * @return the start time
     */
    public LocalDateTime getStart() { return start; }

    /**
     * Sets the end time of the workday
     * @param time the end time of the workday
     */
    public void setEnd(LocalDateTime time) { this.end = time; }
    /**
     * gets the end of the workday
     * @return end the end time of the workday
     */
    public LocalDateTime getEnd() { return end; }

    /**
     * Overwrites the list of Pomodoros
     * @param pomodoros the new pomodoro list
     */
    public void setPomodoros(ArrayList<Pomodoro> pomodoros) { this.pomodoros = pomodoros; }

    /**
     * Gets the list of Pomodoros in the workday
     * @return Pomodoro object list
     */
    public ArrayList<Pomodoro> getPomodoros() { return pomodoros; }

    /**
     * Sets the updated string of the workday
     * @param updated Updated string
     */
    public void setUpdated(String updated) { this.updated = updated; }
    /**
     * Gets the last updated time of the workday
     * @return updated when the workday was last updated.
     */
    public String getUpdated() { return updated; }
}