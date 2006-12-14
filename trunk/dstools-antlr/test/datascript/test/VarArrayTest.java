/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.AnyItem;
import bits.ItemA;
import bits.ItemB;
import bits.ItemC;
import bits.VarArray;
import datascript.runtime.array.ObjectArray;

/**
 * @author HWellmann
 *
 */
public class VarArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "vararraytest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public VarArrayTest(String name)
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
    private void writeArray(int numElems, int startValue) throws IOException
    {
        os = new FileImageOutputStream(file);

        for (int i = 0; i < numElems; i++)
        {
            os.writeByte(1);
            os.writeShort(startValue+i);
        }
        os.writeByte(2);
        os.writeInt(startValue);
        os.writeShort(numElems);
        int size = (int)os.getStreamPosition();
        os.close();

        VarArray array = new VarArray(fileName);
        ObjectArray<ItemA> aa = array.getA();
        for (int i = 0; i < aa.length(); i++)
        {
            ItemA a = aa.elementAt(i);
            assertEquals(startValue+i, a.getValue());
        }
        ItemB b = array.getB();
        assertEquals(startValue, b.getValue());
        assertEquals(numElems, array.getLen());
        assertEquals(size, array.sizeof());
    }

    public void testArray1() throws IOException
    {
        writeArray(2, 20);
    }

    public void testArray2() throws IOException
    {
        writeArray(10, 2000);
    }

    public void testArray3() throws IOException
    {
        writeArray(100, 20000);
    }
}
