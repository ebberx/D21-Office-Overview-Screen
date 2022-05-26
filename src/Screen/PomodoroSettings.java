package Screen;

public class PomodoroSettings {
    private String workTime;
    private String breakTime;
    private String longBreakTime;

    public PomodoroSettings(String workTime, String breakTime, String longBreakTime) {
        this.workTime = workTime;
        this.breakTime = breakTime;
        this.longBreakTime = longBreakTime;
    }

    public String getWorkTime() { return workTime; }
    public String getBreakTime() { return breakTime; }
    public String getLongBreakTime() { return longBreakTime; }
}
