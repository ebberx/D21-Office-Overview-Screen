package Test;

import org.junit.Test;
import static org.junit.Assert.*;
import Foundation.DB;

/**
 * Collection of database related tests.
 * @author Esben Christensen
 */
public class DBTest {

    /**
     * Preparatory setup for the tests to be carried out.
     */
    @org.junit.Before
    public void setup() {
        // Establish DB connection to test database
        DB.getInstance().setDatabaseName("TOMATOSOUP");
        DB.getInstance().connect();
    }

    /**
     * Clean-up of tests, deconstruction.
     */
    @org.junit.After
    public void tearDown() {
        // Close connection
        DB.getInstance().disconnect();
    }

    /**
     * Validates the database connection.
     * Effectful.
     */
    @Test
    public void DBConnectionValidation() {
        // Test that connection is valid
        assertTrue(DB.getInstance().validateConnection());
    }
}