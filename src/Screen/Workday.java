package Screen;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Workday {
    int id;
    Consultant consultant;
    LocalDateTime start;
    LocalDateTime end;
    ArrayList<Pomodoro> pomodoros = new ArrayList<Pomodoro>();
    String updated;

    public Workday(int id, Consultant consultant, LocalDateTime start, LocalDateTime end, String updated) {
        this.id = id;
        this.consultant = consultant;
        this.start = start;
        this.end = end;
        this.updated = updated;
    }

    public int                 getId()         { return id; }
    public Consultant          getConsultant() { return consultant; }
    public LocalDateTime       getStart()      { return start; }
    public LocalDateTime       getEnd()        { return end; }
    public ArrayList<Pomodoro> getPomodoros()  { return pomodoros; }
    public String              getUpdated()    { return updated; }
}