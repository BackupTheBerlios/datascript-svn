/**
 * 
 */


package datascript.test;


import java.io.File;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import sqlParameter.MyBlob;
import sqlParameter.SqlTestDb;
import datascript.runtime.SqlDatabase.Mode;
import datascript.runtime.array.BitFieldArray;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;

import junit.framework.TestCase;



/**
 * @author HWedekind
 * 
 */
public class SqlParameterTest extends TestCase
{
    private static final String fileName = "sqlparameter.db3";
    private File file = new File(fileName);
    
    private Connection dbc = null;
    private Statement st = null;


    /**
     * Constructor for BitStreamReaderTest.
     * 
     * @param name
     */
    public SqlParameterTest(String name)
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
            // file.delete();
        }
    }


    private MyBlob writeFoo(PreparedStatement pst, short bitSize, short elementCount) throws Exception
    {
        BigInteger[] bia = new BigInteger[elementCount];
        for(int i = 0; i < elementCount; i++)
            bia[i] = BigInteger.valueOf(i & ((1 << bitSize) -1));
        BitFieldArray flags = new BitFieldArray(bia, 0, elementCount, bitSize);
        MyBlob b1 = new MyBlob(flags);
        b1.setP1(bitSize);
        b1.setP2(elementCount);
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        b1.write(writer);
        writer.close();
        pst.setLong(1, 1);
        pst.setBytes(2, writer.toByteArray());

        int rows = pst.executeUpdate();
        assertEquals(rows, 1);
        
        return b1;
    }


    private void writeFoo1() throws Exception
    {
        short bitSize = 2;
        short elementCount = 3;

        PreparedStatement pst = dbc.prepareStatement("insert into foo1 values (?, ?)");
        MyBlob b1 = writeFoo(pst, bitSize, elementCount);

        ResultSet rs = st.executeQuery("select * from foo1");
        assertTrue(rs.next());

        long id = rs.getLong("id");
        byte[] data = rs.getBytes("data");
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(data);
        MyBlob b1a = new MyBlob(reader, bitSize, elementCount);
        reader.close();
        assertEquals(1, id);
        assertEquals(b1, b1a);

        //assertTrue(rs.next());

        rs.close();
        st.close();
    }


    private void writeFoo2() throws Exception
    {
        short bitSize = 6;
        short elementCount = 2;

        PreparedStatement pst = dbc.prepareStatement("insert into foo2 values (?, ?)");
        MyBlob b2 = writeFoo(pst, bitSize, elementCount);

        ResultSet rs = st.executeQuery("select * from foo2");
        assertTrue(rs.next());

        long id = rs.getLong("id");
        byte[] data = rs.getBytes("data");
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(data);
        MyBlob b2a = new MyBlob(reader, bitSize, elementCount);
        reader.close();
        assertEquals(1, id);
        assertEquals(b2, b2a);

        //assertTrue(rs.next());

        rs.close();
        st.close();
    }


    public void testCreationReadWrite() throws Exception
    {
        SqlTestDb db = new SqlTestDb(fileName, Mode.WRITE);
        dbc = db.getConnection();
        st = dbc.createStatement();

        ResultSet rs = st.executeQuery("select * from foo1");
        assertFalse(rs.next());
        rs.close();
        rs = st.executeQuery("select * from foo2");
        assertFalse(rs.next());
        rs.close();
        st.close();

        writeFoo1();
        writeFoo2();

        db.close();
    }

}
