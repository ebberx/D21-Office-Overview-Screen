package Screen;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author tha
 */
public class DB {
    private static Connection con;
    private static String port;
    private static String databaseName;
    private static String userName;
    private static String password;

    private DB(){
    }

    static {
        Properties props = new Properties();
        String fileName = "db.properties";
        InputStream input;
        try{
            input = new FileInputStream(fileName);
            props.load(input);
            port = props.getProperty("port","1433");
            databaseName = props.getProperty("databaseName");
            userName=props.getProperty("userName", "SA");
            password=props.getProperty("password");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            //System.out.println("Database Ready");

        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    private static void connect(){
        try {
            con = DriverManager.getConnection("jdbc:sqlserver://51.75.69.121:"+port+";databaseName="+databaseName,userName,password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }
    private static void disconnect(){
        try {
            con.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}

