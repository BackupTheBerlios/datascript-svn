/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.ItemA;
import bits.ItemB;
import bits.arrays.VarArray;
import datascript.runtime.array.ObjectArray;

/**
 * @author HWellmann
 *
 */
public class VarArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "VarArryWriteTest.data";
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
    
    private void checkArray(VarArray array, int size, int numElems, int startValue)
    {
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

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeArray(int numElems, int startValue) throws IOException
    {
    	file.delete();
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

        return size;
    }

    public void testArray1() throws Exception
    {
        int size = writeArray(2, 20);
        VarArray array = new VarArray(fileName);
        checkArray(array, size, 2, 20);

        array.write(wFileName);

        VarArray array2 = new VarArray(wFileName);
        checkArray(array2, size, 2, 20);
        assertTrue(array.equals(array2));
    }

    public void testArray2() throws Exception
    {
        int size = writeArray(10, 2000);
        VarArray array = new VarArray(fileName);
        checkArray(array, size, 10, 2000);

        array.write(wFileName);

        VarArray array2 = new VarArray(wFileName);
        checkArray(array2, size, 10, 2000);
        assertTrue(array.equals(array2));
    }

    public void testArray3() throws Exception
    {
        int size = writeArray(100, 20000);
        VarArray array = new VarArray(fileName);
        checkArray(array, size, 100, 20000);

        array.write(wFileName);

        VarArray array2 = new VarArray(wFileName);
        checkArray(array2, size, 100, 20000);
        assertTrue(array.equals(array2));
    }
}
