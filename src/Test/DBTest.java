package Test;

import org.junit.Test;
import static org.junit.Assert.*;
import Screen.DB;

public class DBTest {

    @org.junit.Before
    public void setup() {
        // Establish DB connection to test database
        DB.getInstance().setDatabaseName("TOMATOSOUP");
        DB.getInstance().connect();
    }

    @org.junit.After
    public void tearDown() {
        // Close connection
        DB.getInstance().disconnect();
    }

    @Test
    public void DBConnectionValidation() {
        // Test that it works
        assertTrue(DB.getInstance().validateConnection());
    }
}