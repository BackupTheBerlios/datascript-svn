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
import bits.VarArrayWithSize;
import bits.arrays.VarArray;
import datascript.runtime.array.ObjectArray;



/**
 * @author HWellmann
 * 
 */
public class VarArrayWithSizeTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "VarArraySizeWriteTest.data";
    private String fileName = "vararraysizetest.bin";
    private File file = new File(fileName);


    /**
     * Constructor for BitStreamReaderTest.
     * 
     * @param name
     */
    public VarArrayWithSizeTest(String name)
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


    private void checkArray(VarArrayWithSize arrayWithSize, int size, 
            int numElems, int startValue)
    {
        VarArray array = arrayWithSize.getVar();
        ObjectArray<ItemA> aa = array.getA();
        for (int i = 0; i < aa.length(); i++)
        {
            ItemA a = aa.elementAt(i);
            assertEquals(startValue + i, a.getValue());
        }
        ItemB b = array.getB();
        assertEquals(startValue, b.getValue());
        assertEquals(numElems, array.getLen());
        assertEquals(size-4, array.sizeof());

        assertEquals(size-4, arrayWithSize.getSize());
        assertEquals((size-4)*8, arrayWithSize.getBitsize());
        assertEquals(size, arrayWithSize.sizeof());
    }


    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeArray(int numElems, int startValue) throws IOException
    {
        int size = 0;
        file.delete();
        os = new FileImageOutputStream(file);

        // ItemA
        for (int i = 0; i < numElems; i++)
        {
            os.writeByte(1);
            os.writeShort(startValue + i);
        }

        // ItemB
        os.writeByte(2);
        os.writeInt(startValue);

        // len
        os.writeShort(numElems);

        size = (int) os.getStreamPosition();
        os.writeShort(size);
        os.writeShort(size*8);

        size = (int) os.getStreamPosition();
        os.close();

        return size;
    }


    public void testArray1() throws Exception
    {
        int size = writeArray(2, 20);
        VarArrayWithSize array = new VarArrayWithSize(fileName);
        checkArray(array, size, 2, 20);

        array.write(wFileName);

        VarArrayWithSize array2 = new VarArrayWithSize(wFileName);
        checkArray(array2, size, 2, 20);
        assertTrue(array.equals(array2));
    }


    public void testArray2() throws Exception
    {
        int size = writeArray(10, 2000);
        VarArrayWithSize array = new VarArrayWithSize(fileName);
        checkArray(array, size, 10, 2000);

        array.write(wFileName);

        VarArrayWithSize array2 = new VarArrayWithSize(wFileName);
        checkArray(array2, size, 10, 2000);
        assertTrue(array.equals(array2));
    }


    public void testArray3() throws Exception
    {
        int size = writeArray(100, 20000);
        VarArrayWithSize array = new VarArrayWithSize(fileName);
        checkArray(array, size, 100, 20000);

        array.write(wFileName);

        VarArrayWithSize array2 = new VarArrayWithSize(wFileName);
        checkArray(array2, size, 100, 20000);
        assertTrue(array.equals(array2));
    }
}
