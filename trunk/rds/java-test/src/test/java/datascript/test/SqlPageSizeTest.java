/**
 * 
 */


package datascript.test;


import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import sqltest.SqlTestDb;



/**
 * @author HWellmann
 * 
 */
public class SqlPageSizeTest extends TestCase
{
    private String fileName = "sqlpagesize.db3";
    private File file = new File(fileName);


    /**
     * Constructor for BitStreamReaderTest.
     * 
     * @param name
     */
    public SqlPageSizeTest(String name)
    {
        super(name);
    }


    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        file.delete();
    }


    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        if (file.exists())
        {
            file.delete();
        }
    }


    public void testPragma() throws SQLException
    {
    	System.out.println(file.exists());
        Connection dbc = DriverManager.getConnection("jdbc:sqlite:" + fileName);
        SqlTestDb db = new SqlTestDb(dbc);
        Statement st = dbc.createStatement();
        st.executeUpdate("pragma page_size = 4096");
        st.close();
        
        db.createSchema();
        
        ResultSet rs = st.executeQuery("pragma page_size");
        if (rs.next())
        {
            int pageSize = rs.getInt(1);
            assertEquals(4096, pageSize);
        }
        else
        {
            fail("cannot read pragma page_size");
        }
        st.close();
        rs.close();
        
        db.close();
    }
}
