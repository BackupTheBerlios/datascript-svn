/**
 * 
 */


package datascript.test;


import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
    @Override
    protected void setUp() throws Exception
    {
        file.delete();
        SqlTestDb db = new SqlTestDb(fileName, Mode.CREATE);
        db.close();
    }


    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        if (file.exists())
        {
            // file.delete();
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

        PreparedStatement pst = dbc
                .prepareStatement("insert into tiles values (?, ?)");
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

        int rows = st.executeUpdate("insert into levels values ('test1', 1, 'blob1', 123, 1234567, 3, x'0505050505')");
        assertEquals(rows, 1);

        rows = st.executeUpdate("insert into levels values ('test1', 2, 'blob2', 123, 1234567, 3, x'0505050505')");
        assertEquals(rows, 1);

        rows = st.executeUpdate("insert into levels values ('test1', 3, 'blob3', 123, 1234567, 3, x'0505050505')");
        assertEquals(rows, 1);

        try
        {
            rows = st.executeUpdate("insert into levels values ('test1', 1, 'blob4', 123, 1234567, 3, x'0505050505')");
            assertTrue(false);
        }
        catch (SQLException exc)
        {
            assertEquals(exc.getMessage(), "PRIMARY KEY must be unique" /*"column levelNr is not unique"*/);
        }

        db.close();
    }


    public void testTypes() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
        DatabaseMetaData metaData = db.getConnection().getMetaData();

        ResultSet columns = metaData.getColumns(null, null, "levels", "testName");
        String typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "VARCHAR");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "levelNr");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "INTEGER");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "level");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "BLOB");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "val1");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "CHAR(3)");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "val2");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "INTEGER");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "val3");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "INTEGER");
        columns.close();

        columns = metaData.getColumns(null, null, "levels", "val4");
        typeName = columns.getString("TYPE_NAME");
        assertEquals(typeName, "BLOB");
        columns.close();

        db.close();
    }


    public void testInsertionTableReference() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
        Statement st = db.getConnection().createStatement();

        int rows = st.executeUpdate("insert into moreLevels values ('test2', 1, 'blob1', 12345, 1234567, 3, x'0505050505')");
        assertEquals(rows, 1);

        rows = st.executeUpdate("insert into moreLevels values ('test2', 2, 'blob2', 12345, 1234567, 3, x'0505050505')");
        assertEquals(rows, 1);

        db.close();
    }


    private void packAndUnpackSqlInteger(byte nu1, short level, long tileNum)
            throws Exception
    {
        TileId id = new TileId(nu1, level, tileNum);
        assertEquals(id.getNu1(), nu1);
        assertEquals(id.getLevel(), level);
        assertEquals(id.getTileId(), tileNum);

        long packed = id.pack();
        TileId id2 = new TileId(packed);
        assertTrue(id.equals(id2));
        assertEquals(id2.getNu1(), nu1);
        assertEquals(id2.getLevel(), level);
        assertEquals(id2.getTileId(), tileNum);
    }


    public void testSqlInteger1() throws Exception
    {
        packAndUnpackSqlInteger((byte) 5, (short) 10, 123456);
    }


    public void testSqlInteger2() throws Exception
    {
        // now use unsigned values outside of the signed range
        packAndUnpackSqlInteger((byte) -5, (short) 150, 0x00CAFEBABEL);
    }

}
