/**
 * 
 */
package datascript.test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import sqltest.SqlTestDb;
import datascript.runtime.SqlDatabase.Mode;

/**
 * @author HWellmann
 *
 */
public class SqlTableTest extends TestCase
{
    private String fileName = "sqltest.db3";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public SqlTableTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
    	file.delete();
    	SqlTestDb db = new SqlTestDb(fileName, Mode.CREATE);
    	db.close();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        if (file.exists())
        {
        	file.delete();
        }
    }
    
    
    
    public void testCreation() throws Exception
    {
    	SqlTestDb db = new SqlTestDb(fileName, Mode.READONLY);
    	assertTrue(file.exists());
    	Connection dbc = db.getConnection();
    	Statement st = dbc.createStatement();
    	
    	ResultSet rs = st.executeQuery("select * from levels");
    	assertFalse(rs.next());
    	db.close();    	
    }

    public void testCreation2() throws Exception
    {
    	SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
    	Connection dbc = db.getConnection();
    	Statement st = dbc.createStatement();
    	
    	ResultSet rs = st.executeQuery("select * from levels");
    	assertFalse(rs.next());
    	rs.close();
    	st.close();
    	
    	rs = st.executeQuery("select * from tiles");
    	assertFalse(rs.next());
    	rs.close();
    	st.close();
    	db.close();
    }

    public void testInsertion() throws Exception
    {
    	SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
    	Connection dbc = db.getConnection();
    	Statement st = dbc.createStatement();
    	
        int rows = st.executeUpdate("insert into levels values (1, 'blob1')");
    	assertEquals(rows, 1);

    	rows = st.executeUpdate("insert into levels values (2, 'blob2')");
    	assertEquals(rows, 1);

    	rows = st.executeUpdate("insert into levels values (3, 'blob3')");
    	assertEquals(rows, 1);

    	try
    	{
    		rows = st.executeUpdate("insert into levels values (1, 'blob4')");
    		assertTrue(false);
    	}
    	catch (SQLException exc)
    	{
    		assertEquals(exc.getMessage(), "column levelNr is not unique");
    	}

    	db.close();
    }
}
