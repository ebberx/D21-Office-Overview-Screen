package Domain;

import java.util.ArrayList;

/**
 * Wrapper class for the database abstraction of a consultant.
 * @author Esben Christensen
 */
public class Consultant {

    /**
     * The email of the consultant. Primary key in database.
     */
    private String email;
    /**
     * The name of the consultant
     */
    private String name;
    /**
     * Current status of consultant
     */
    private String status;
    /**
     * List of consultants' workdays after current date at midnight
     */
    private ArrayList<Workday> workdays;

    /**
     * Constructor
     * @param email the consultants email
     * @param name the consultants name
     */
    public Consultant(String email, String name) {
        this.email = email;
        this.name = name;
    }

    /**
     * Get email
     * @return the email of the consultant
     */
    public String getEmail() { return email; }

    /**
     * Get name
     * @return the name of the consultant
     */
    public String getName() { return name; }

    /**
     * get status
     * @return the current status of the consultant
     */
    public String getStatus() { return status; }

    /**
     * sets the the status of the consultant
     * @param status status to set
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * get list of workdays
     * @return the consultants workdays
     */
    public ArrayList<Workday> getWorkdays() { return workdays; }

    /**
     * Sets the workdays to a new list of workdays
     * @param workdays the list of workdays to set
     */
    public void setWorkdays(ArrayList<Workday> workdays) { this.workdays = workdays; }
}
