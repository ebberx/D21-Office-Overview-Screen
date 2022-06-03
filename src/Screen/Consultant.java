package Screen;

import java.util.ArrayList;

public class Consultant {

    private String email;
    private String name;
    private String status;
    private ArrayList<Workday> workdays;

    public Consultant(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public ArrayList<Workday> getWorkdays() { return workdays; }
    public void setWorkdays(ArrayList<Workday> workdays) { this.workdays = workdays; }
}
