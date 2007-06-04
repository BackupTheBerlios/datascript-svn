/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.Header;
import bits.Inner;
import bits.Outer;
import datascript.runtime.array.ShortArray;

/**
 * @author HWellmann
 *
 */
public class ContainmentTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "ContainmentTest.data";
    private String fileName = "containment.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public ContainmentTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        file.delete();
    }

    private void checkData(Outer outer, int size, short a, short length, short c, byte d, short e)
    {
        assertEquals(a, outer.getA());
        Header header = outer.getHeader();
        Inner  inner  = outer.getInner();
        assertEquals(length, header.getLen());
        assertEquals(c, header.getC());
        assertEquals(d, inner.getD());
        assertEquals(e, inner.getE());
        for (int i = 0; i < length; i++)
        {
            ShortArray array = inner.getList();
            assertEquals(20*i, array.elementAt(i));
        }        
        assertEquals(size, outer.sizeof());
    }
    
    private int writeData(short a, short length, short c, byte d, short e) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeShort(a);
        os.writeShort(length);
        os.writeShort(c);
        os.writeByte(d);
        for (int i = 0; i < length; i++)
        {
            os.writeShort(20*i);
        }
        os.writeInt(e);
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    public void testContainment() throws IOException
    {
        short a = 99;
        short length = 5;
        short c = -4711;
        byte  d = 3;
        short e = 1234;
        
        int size = writeData(a, length, c, d, e);
        Outer outer = new Outer(fileName);
        checkData(outer, size, a, length, c, d, e);
        
        outer.write(wFileName);

        Outer outer2 = new Outer(wFileName);
        checkData(outer2, size, a, length, c, d, e);
        assertTrue(outer.equals(outer2));
    }
}
