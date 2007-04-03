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
import bits.MixedArray;

/**
 * @author HWellmann
 *
 */
public class MixedArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "CompoundArryWriteTest.data";
    private String fileName = "mixedarraytest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public MixedArrayTest(String name)
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
    
    private void checkArray(MixedArray array, int size, 
    		int numElems, int startValue) throws IOException
    {
        assertEquals(numElems, array.getNumItems());

        for (int i = 0; i < numElems; i++)
        {
            AnyItem item = array.getItems().elementAt(i);
            switch (i % 3)
            {
                case 0:
                    assertTrue(item.isA());
                    ItemA a = item.getA();
                    assertEquals(startValue+i, a.getValue());
                    break;
                case 1:
                    assertTrue(item.isB());
                    ItemB b = item.getB();
                    assertEquals(startValue+i, b.getValue());
                    break;
                case 2:
                    assertTrue(item.isC());
                    ItemC c = item.getC();
                    assertEquals(startValue+i, c.getValue());
                    break;
            }
        }
        assertEquals(size, array.sizeof());
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     * return	size of the file
     */
    private int writeArray(int numElems, int startValue) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(numElems);

        for (int i = 0; i < numElems; i++)
        {
            switch (i % 3)
            {
                case 0:
                    os.writeByte(1);
                    os.writeShort(startValue+i);
                    break;
                case 1:
                    os.writeByte(2);
                    os.writeInt(startValue+i);
                    break;
                case 2:
                    os.writeByte(3);
                    os.writeLong(startValue+i);
                    break;
            }
        }
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }

    public void testArray1() throws IOException
    {
        int size = writeArray(10, 20);
        MixedArray array = new MixedArray(fileName);
        checkArray(array, size, 10, 20);

        array.write(wFileName);

        MixedArray array2 = new MixedArray(fileName);
        checkArray(array2, size, 10, 20);
        assertTrue(array.equals(array2));
    }

    public void testArray2() throws IOException
    {
    	int size = writeArray(10, 20);
        MixedArray array = new MixedArray(fileName);
        checkArray(array, size, 10, 20);

        array.write(wFileName);

        MixedArray array2 = new MixedArray(fileName);
        checkArray(array2, size, 10, 20);
        assertTrue(array.equals(array2));
    }

    public void testArray3() throws IOException
    {
        int size = writeArray(1000, 20000);
        MixedArray array = new MixedArray(fileName);
        checkArray(array, size, 1000, 20000);

        array.write(wFileName);

        MixedArray array2 = new MixedArray(fileName);
        checkArray(array2, size, 1000, 20000);
        assertTrue(array.equals(array2));
    }

    public void testEmptyArray() throws IOException
    {
        int size = writeArray(0, 0);
        MixedArray array = new MixedArray(fileName);
        checkArray(array, size, 0, 0);

        array.write(wFileName);

        MixedArray array2 = new MixedArray(fileName);
        checkArray(array2, size, 0, 0);
        assertTrue(array.equals(array2));
    }
}
