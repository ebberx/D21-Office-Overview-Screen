package Foundation;

import Domain.Consultant;
import Domain.Pomodoro;
import Domain.Workday;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Database wrapper for JBDC. Specific methods for retrieving data for the operation of the pomodoro schedule application.
 * Manual connect and disconnect. Database to be connected to specified in configuration file in root folder.
 * @author Esben Christensen
 */
public class DB {
    /**
     * The singleton instance of the DbB class
     */
    private static DB instance;
    /**
     * Connection object to the database
     */
    private Connection con;
    /**
     * Host of the database server
     */
    private String host;
    /**
     * Port of the database server
     */
    private String port;
    /**
     * Database name to use in the database server
     */
    private String databaseName;
    /**
     * Database server username
     */
    private String userName;
    /**
     * Database server password
     */
    private String password;

    /**
     * Gets the singleton instance of the DB class. If it is not created one will be created.
     * @return The singleton instance.
     */
    public static DB getInstance() {
        if(instance != null)
            return instance;

        instance = new DB();
        return instance;
    }

    /**
     * Private contructor that loads configuration settings from db.properties located in the root folder.
     */
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
     * Establishes a connection to the database.
     */
    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":"+port+";databaseName="+databaseName,userName,password);
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Disconnects the connection to the database.
     */
    public void disconnect() {
        try {
            con.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Validates the connection to the server.
     * @return True if connection is valid, false if otherwise.
     */
    public boolean validateConnection() {
        try {
            return con.isValid(1000);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the databse name that should be used when connecting to the databse server.
     * @return the database name for future connections.
     */
    public String getDatabaseName() { return databaseName; }

    /**
     * Sets the database name that the DB class should connect to.
     * In order to change databse, it is necessary to disconnect and reconnect.
     * @param dbName the name of the database in the MS SQL server
     */
    public void setDatabaseName(String dbName) { this.databaseName = dbName; }

    /**
     * Invokes stored procedure that takes a String officeName, and returns associated Consultants
     * @param officeName The office name of which to get Consultants
     * @return ArrayList of the Consultants in given office
     */
    public ArrayList<Consultant> getConsultantsInOffice(String officeName) {
        try {
            // Query stored procedure
            PreparedStatement ps = con.prepareStatement("{ call getConsultantsInOffice (?) }");
            ps.setString(1, officeName);
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                System.err.println("[getConsultantsInOffice] No consultants in office");
                return null;
            }

            // Get consultants list
            ArrayList<Consultant> consultants = new ArrayList<>();
            while(rs.next()) {
                // Data from ResultSet
                String email    = rs.getString(1);
                String name     = rs.getString(2);
                int order       = rs.getInt(3);
                String office   = rs.getString(4);
                String pom_time = rs.getString(5);
                String b_time   = rs.getString(6);
                String lb_time  = rs.getString(7);
                boolean active  = rs.getBoolean(8);
                String status   = rs.getString(9);

                // Create new consultant object and add
                Consultant c = new Consultant(email, name);
                c.setStatus(status);
                consultants.add(c);
            }

            return consultants;
        }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Invokes stored procedure that takes Consultant id, and gets the Workdays of the given Consultant.
     * @param consultant Consultant of which to get Workdays
     * @param dateTime The point in time after which workdays are queried.
     */
    public void getWorkdaysOfConsultant(Consultant consultant, String dateTime) {
        try {
            // Query stored procedure
            PreparedStatement ps = con.prepareStatement("{ call getWorkdaysOfConsultant (?,?) }");
            ps.setString(1, consultant.getEmail());
            ps.setString(2, dateTime);
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                System.err.println("[getWorkdaysOfConsultant]: Empty resultset received.");
                return;
            }

            // Get workday list for given consultant
            ArrayList<Workday> workdays = new ArrayList<>();
            while(rs.next()) {
                // Data from ResultSet
                int id                 = rs.getInt(1);
                String consultantEmail = rs.getString(2);
                String start           = rs.getString(3);
                String end             = rs.getString(4);
                String updated         = rs.getString(5);

                // Create a new consultant object
                try {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                    workdays.add(new Workday(id, LocalDateTime.parse(start, df), LocalDateTime.parse(end, df), updated));
                }
                catch (Exception e) { e.printStackTrace(); }

                // Assign workdays to consultant
                consultant.setWorkdays(workdays);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Invokes stored procedure that takes Workday id, and gets back Pomodoros of the workday.
     * These are then put into the given Workday.
     * @param workday The workday of which to get Pomodoros
     */
    public void getPomodorosInWorkday(Workday workday) {
        try {
            PreparedStatement ps = con.prepareStatement("{ call getPomodorosInWorkday (?) }");
            ps.setInt(1, workday.getId());
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet throw exception
            if (!rs.isBeforeFirst() ) {
                System.err.println("[getPomodorosInWorkday]: Empty resultset received.");
                return;
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
                Duration work_time_dur      = Duration.between(LocalTime.parse("00:00:00"), LocalTime.parse(work_time));
                Duration break_time_dur     = Duration.between(LocalTime.parse("00:00:00"), LocalTime.parse(break_time));
                DateTimeFormatter df        = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDateTime startDateTime = LocalDateTime.parse(start, df);
                LocalDateTime endDateTime   = LocalDateTime.parse(end, df);

                // Create Pomodoro and add to list
                pomodoros.add(new Pomodoro(work_time_dur, break_time_dur, startDateTime, endDateTime));
            }

            // Assign newly received Pomodoros to workday
            workday.setPomodoros(pomodoros);

        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Invokes stored procedure that takes Consulant id, and gets the current Status of the given Consulant.
     * @param consultant Consultant of which to get the current status
     */
    public void getStatusOfConsultant(Consultant consultant) {
        try {
            PreparedStatement ps = con.prepareStatement("{ call getStatusOfConsultant (?) }");
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

    /**
     * Queries the workday for updates. If there are updates overwrite the workday.
     * @param workday Workday to query for updates.
     * @return true if update received, false otherwise
     */
    public boolean getWorkdayUpdated(Workday workday) {
        try {
            PreparedStatement ps = con.prepareStatement("{ call getWorkdayUpdated (?,?) }");
            ps.setInt(1, workday.getId());
            ps.setString(2, workday.getUpdated());
            ResultSet rs = ps.executeQuery();

            // If we receive an empty ResultSet that means that the workday was not updated
            if (!rs.isBeforeFirst() ) {
                return false;
            }

            while(rs.next()) {
                int id                 = rs.getInt(1);
                String consultantEmail = rs.getString(2);
                String start           = rs.getString(3);
                String end             = rs.getString(4);
                String updated         = rs.getString(5);

                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                workday.setID(id);
                workday.setStart(LocalDateTime.parse(start, df));
                workday.setEnd(LocalDateTime.parse(end, df));
                workday.setUpdated(updated);
            }
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

