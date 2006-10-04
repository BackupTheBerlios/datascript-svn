/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.CompoundArray;
import bits.ItemA;
import bits.ItemB;
import datascript.runtime.ObjectArray;

/**
 * @author HWellmann
 *
 */
public class CompoundArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "arrayparsertest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public CompoundArrayTest(String name)
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

    public void testArray1() throws IOException
    {
        writeArray(5, 20, 100000);
    }

    public void testArray2() throws IOException
    {
        writeArray(5000, 29000, 100000);
    }
}
