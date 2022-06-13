package Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Workday {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    ArrayList<Pomodoro> pomodoros = new ArrayList<Pomodoro>();
    String updated;

    public Workday(int id, LocalDateTime start, LocalDateTime end, String updated) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.updated = updated;
    }

    public void setID(int id) { this.id = id; }
    public int getId() { return id; }
    public void setStart(LocalDateTime time) { this.start = time; }
    public LocalDateTime getStart() { return start; }
    public void setEnd(LocalDateTime time) { this.end = time; }
    public LocalDateTime getEnd() { return end; }
    public void setPomodoros(ArrayList<Pomodoro> pomodoros) { this.pomodoros = pomodoros; }
    public ArrayList<Pomodoro> getPomodoros() { return pomodoros; }
    public void setUpdated(String updated) { this.updated = updated; }
    public String getUpdated() { return updated; }
}