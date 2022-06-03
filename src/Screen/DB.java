package Screen;

import javax.xml.transform.Result;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author tha
 */
public class DB {

    private static DB instance;
    private Connection con;
    private String host;
    private String port;
    private String databaseName;
    private String userName;
    private String password;

    public static DB getInstance() {
        if(instance != null)
            return instance;

        instance = new DB();
        return instance;
    }

    private DB(){
        Properties props = new Properties();
        String fileName = "db.properties";
        InputStream input;
        try {
            input = new FileInputStream(fileName);
            props.load(input);
            host = props.getProperty("host","51.75.69.121");
            port = props.getProperty("port","1433");
            databaseName = props.getProperty("databaseName");
            userName=props.getProperty("userName", "SA");
            password=props.getProperty("password");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Establishes a connection to the database
     */
    private void connect() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":"+port+";databaseName="+databaseName,userName,password);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Disconnects the connection to the database
     */
    private void disconnect() {
        try {
            con.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Invokes stored procedure that takes a String officeName, and returns associated Consultants
     * @param officeName The office name of which to get Consultants
     * @return ArrayList of the Consultants in given office
     */
    public ArrayList<Consultant> getConsultantsInOffice(String officeName) {
        try {
            // Query stored procedure
            PreparedStatement ps = con.prepareStatement("call getConsultantsInOficce ?");
            ps.setString(1, officeName);
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                throw new SQLException();
            }

            // Get consultants list
            ArrayList<Consultant> consultants = new ArrayList<>();
            while(rs.next()) {
                // Data from ResultSet
                String email    = rs.getString(1);
                String name     = rs.getString(2);
                String office   = rs.getString(3);
                String pom_time = rs.getString(4);
                String b_time   = rs.getString(5);
                String lb_time  = rs.getString(6);
                boolean active  = rs.getBoolean(7);
                int order       = rs.getInt(8);

                // Create new consultant object
                consultants.add(new Consultant(email, name));
            }

            return consultants;
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Invokes stored procedure that takes Consultant id, and gets the Workdays of the given Consultant.
     * @param consultant Consultant of which to get Workdays
     * @return ArrayList of the Consultants' Workdays
     */
    public ArrayList<Workday> getWorkdaysOfConsultant(Consultant consultant) {
        try {
            // Query stored procedure
            PreparedStatement ps = con.prepareStatement("call getWorkdaysOfConsultant ?");
            ps.setString(1, consultant.getEmail());
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                throw new SQLException();
            }

            // Get workday list for given consultant
            ArrayList<Workday> workdays = new ArrayList<>();
            while(rs.next()) {
                // Data from ResultSet
                int id                 = rs.getInt(1);
                String consultantEmail = rs.getString(2);
                String start           = rs.getString(3);
                String end             = rs.getString(4);
                String pom_time        = rs.getString(5);
                String b_time          = rs.getString(5);
                String lb_time         = rs.getString(6);
                String updated         = rs.getString(7);

                // Create a new consultant object
                try {
                    workdays.add(new Workday(id, consultant, LocalDateTime.parse(start), LocalDateTime.parse(end), updated));
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            return workdays;
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Invokes stored procedure that takes Workday id, and gets back Pomodoros of the workday.
     * These are then put into the given Workday.
     * @param workday The workday of which to get Pomodoros
     */
    public void getPomodorosInWorkday(Workday workday) {
        try {
            PreparedStatement ps = con.prepareStatement("call getPomodorosInWorkday ?");
            ps.setInt(1, workday.id);
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                throw new SQLException();
            }

            // Get list of pomodoros
            ArrayList<Pomodoro> pomodoros = new ArrayList<>();
            while(rs.next()) {
                // Data from ResultSet
                int workday_id    = rs.getInt(1);
                String start      = rs.getString(2);
                String end        = rs.getString(3);
                String work_time  = rs.getString(4);
                String break_time = rs.getString(5);

                // Convert data to java data types
                Duration work_time_dur      = Duration.between(LocalTime.parse("00:00:00"), LocalDateTime.parse(work_time).toLocalTime());
                Duration break_time_dur     = Duration.between(LocalTime.parse("00:00:00"), LocalDateTime.parse(break_time).toLocalTime());
                LocalDateTime startDateTime = LocalDateTime.parse(start);
                LocalDateTime endDateTime   = LocalDateTime.parse(end);

                // Create Pomodoro and add to list
                pomodoros.add(new Pomodoro(work_time_dur, break_time_dur, startDateTime, endDateTime));
            }

            // Assign newly received Pomodoros to workday
            workday.pomodoros = pomodoros;

        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Invokes stored procedure that takes Consulant id, and gets the current Status of the given Consulant.
     * @param consultant Consultant of which to get the current status
     */
    public void getStatusOfConsultant(Consultant consultant) {
        try {
            PreparedStatement ps = con.prepareStatement("getStatusOfConsultant ?");
            ps.setString(1, consultant.getEmail());
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                throw new SQLException();
            }

            while(rs.next()) {
                String status = rs.getString(1);
                consultant.setStatus(status);
            }

        }
        catch (Exception e) { e.printStackTrace(); }
    }

}

