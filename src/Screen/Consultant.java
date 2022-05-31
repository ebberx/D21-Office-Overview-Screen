package Screen;

public class Consultant {

    private String email;
    private String name;
    private Status status;
    private PomodoroSettings ps;

    public Consultant(String email, String name, Status status, PomodoroSettings ps) {
        this.email = email;
        this.name = name;
        this.status = status;
        this.ps = ps;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public Status getStatus() { return status; }
    public PomodoroSettings getSettings() { return ps; }
}
