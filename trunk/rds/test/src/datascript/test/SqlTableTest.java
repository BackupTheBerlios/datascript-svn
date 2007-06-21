/**
 * 
 */


package datascript.test;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import sqltest.SqlTestDb;
import sqltest.TileId;
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
     * 
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
            //file.delete();
        }
    }


    public void testCreationReadOnly() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.READONLY);
        assertTrue(file.exists());
        Connection dbc = db.getConnection();
        Statement st = dbc.createStatement();

        ResultSet rs = st.executeQuery("select * from levels");
        assertFalse(rs.next());
        st.close();
        rs.close();

        rs = st.executeQuery("select * from moreLevels");
        assertFalse(rs.next());
        st.close();
        rs.close();

        rs = st.executeQuery("select * from tiles");
        assertFalse(rs.next());
        st.close();
        rs.close();

        db.close();
    }


    public void testCreationReadWrite() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
        Connection dbc = db.getConnection();
        Statement st = dbc.createStatement();

        ResultSet rs = st.executeQuery("select * from levels");
        assertFalse(rs.next());
        rs.close();
        st.close();

        rs = st.executeQuery("select * from moreLevels");
        assertFalse(rs.next());
        rs.close();
        st.close();

        PreparedStatement pst = 
            dbc.prepareStatement("insert into tiles values (?, ?)");
        long longVal = (1L << 32) + 123456L;
        pst.setLong(1, longVal);
        pst.setString(2, "blob1");
        int rows = pst.executeUpdate();
        assertEquals(rows, 1);
        longVal = (2L << 32) + 123457L;
        pst.setLong(1, longVal);
        pst.setString(2, "blob2");
        rows = pst.executeUpdate();
        assertEquals(rows, 1);
        longVal = (3L << 32) + 123458L;
        pst.setLong(1, longVal);
        pst.setString(2, "blob3");
        rows = pst.executeUpdate();
        assertEquals(rows, 1);

        rs = st.executeQuery("select * from tiles");
        assertTrue(rs.next());

        long tileid = rs.getLong("tileid");
        TileId id = new TileId(tileid);
        assertEquals(1, id.getLevel());
        assertEquals(123456, id.getTileId());
        assertTrue(rs.next());

        tileid = rs.getLong("tileid");
        id = new TileId(tileid);
        assertEquals(2, id.getLevel());
        assertEquals(123457, id.getTileId());
        assertTrue(rs.next());

        tileid = rs.getLong("tileid");
        id = new TileId(tileid);
        assertEquals(3, id.getLevel());
        assertEquals(123458, id.getTileId());
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


    public void testInsertionTableReference() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
        Connection dbc = db.getConnection();
        Statement st = dbc.createStatement();

        int rows = st
                .executeUpdate("insert into moreLevels values (1, 'blob1')");
        assertEquals(rows, 1);

        rows = st.executeUpdate("insert into moreLevels values (2, 'blob2')");
        assertEquals(rows, 1);

        db.close();
    }
}
