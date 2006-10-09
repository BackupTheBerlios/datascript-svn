/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.CompoundArray;
import bits.Header;
import bits.Inner;
import bits.ItemA;
import bits.ItemB;
import bits.Outer;
import datascript.runtime.ObjectArray;
import datascript.runtime.ShortArray;

/**
 * @author HWellmann
 *
 */
public class ContainmentTest extends TestCase
{
    private FileImageOutputStream os;
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

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private void writeArray(int numItems, int valueA, long valueB) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(numItems);

        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(1);
            os.writeShort(valueA+i);
        }
        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(2);
            os.writeInt((int)valueB+i);
        }
        os.close();

        CompoundArray array = new CompoundArray(fileName);
        ObjectArray a = array.getA();
        assertEquals(numItems, a.length());
        for (int i = 0; i < numItems ; i++)
        {
            ItemA itemA = (ItemA) a.elementAt(i);
            assertEquals(valueA+i, itemA.getValue());
        }
        ObjectArray b = array.getB();
        assertEquals(numItems, b.length());
        for (int i = 0; i < numItems; i++)
        {
            ItemB itemB = (ItemB) b.elementAt(i);
            assertEquals(valueB+i, itemB.getValue());
        }
    }

    public void testContainment() throws IOException
    {
        short a = 99;
        short length = 5;
        short c = -4711;
        byte  d = 3;
        short e = 1234;
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
        os.close();
        
        Outer outer = new Outer(fileName);
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
    }
}
