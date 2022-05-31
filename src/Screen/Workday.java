package Screen;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Workday {
    int id;
    Consultant consultant;
    LocalDateTime start;
    LocalDateTime end;
    PomodoroSettings ps;
    ArrayList<Pomodoro> pomodoros = new ArrayList<Pomodoro>();

    public Workday(int id, Consultant consultant, LocalDateTime start, LocalDateTime end, PomodoroSettings ps) {
        this.id = id;
        this.consultant = consultant;
        this.start = start;
        this.end = end;
        this.ps = ps;
    }

    public int                 getId()         { return id; }
    public Consultant          getConsultant() { return consultant; }
    public LocalDateTime       getStart()      { return start; }
    public LocalDateTime       getEnd()        { return end; }
    public PomodoroSettings    getPs()         { return ps; }
    public ArrayList<Pomodoro> getPomodoros()  { return pomodoros; }
}