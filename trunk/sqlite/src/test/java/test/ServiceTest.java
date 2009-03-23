package test;

import java.io.File;
import java.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

/** These tests check whether access to files is woring correctly and
 *  some Connection.close() cases. */
public class ServiceTest
{
    @Test public void openMemory() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:");
        conn.close();
    }

    @Test public void isClosed() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:");
        assertFalse(conn.isReadOnly());
        conn.close();
        assertTrue(conn.isClosed());
    }

    @Test public void openFile() throws SQLException {
        File testdb = new File("test.db");
        if (testdb.exists()) testdb.delete();

        assertFalse(testdb.exists());
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        assertFalse(conn.isReadOnly());
        conn.close();

        assertTrue(testdb.exists());
        conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        assertFalse(conn.isReadOnly());
        conn.close();

        assertTrue(testdb.exists());
        testdb.delete();
    }

    @Test public void openAbsFile() throws SQLException {
        File testdb = new File("D:/temp/test.db");
        if (testdb.exists()) testdb.delete();

        assertFalse(testdb.exists());
        Connection conn = DriverManager.getConnection("jdbc:sqlite:///D:/temp/test.db");
        assertFalse(conn.isReadOnly());
        conn.close();

        assertTrue(testdb.exists());
        conn = DriverManager.getConnection("jdbc:sqlite:///D:/temp/test.db");
        assertFalse(conn.isReadOnly());
        conn.close();

        assertTrue(testdb.exists());
        testdb.delete();
    }

    @Test(expected= SQLException.class)
    public void closeTest() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:");
        PreparedStatement prep = conn.prepareStatement("select null;");
        prep.executeQuery();
        conn.close();
        prep.clearParameters();
    }
}
