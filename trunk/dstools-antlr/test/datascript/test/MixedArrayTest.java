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

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private void writeArray(int numElems, int startValue) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(numElems);
        int value = startValue;
        

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

        MixedArray array = new MixedArray(fileName);
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

    public void testArray1() throws IOException
    {
        writeArray(10, 20);
    }

    public void testArray2() throws IOException
    {
        writeArray(100, 2000);
    }

    public void testArray3() throws IOException
    {
        writeArray(1000, 20000);
    }
}
