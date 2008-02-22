/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.ParamBlock;
import bits.arrays.ParamArray;
import datascript.runtime.array.ByteArray;
import datascript.runtime.array.UnsignedShortArray;
import datascript.runtime.io.DataScriptIO;

/**
 * @author HWellmann
 * 
 */
public class ParamArrayTest extends TestCase
{
    private FileImageOutputStream os;

    private String wFileName = "ParamArryWriteTest.data";

    private String fileName = "paramarraytest.bin";

    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * 
     * @param name
     */
    public ParamArrayTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        file.delete();
    }

    private void checkArray(ParamArray array, int sizeof, long numItems,
            byte startValue, short[] size)
    {
        assertEquals(numItems, array.getNumItems());
        UnsignedShortArray sizes = array.getSize();
        for (int i = 0; i < numItems; i++)
        {
            assertEquals(size[i], sizes.elementAt(i));
        }
        for (int i = 0; i < numItems; i++)
        {
            ParamBlock block = array.getBlock().elementAt(i);
            for (int j = 0; j < size[i]; j++)
            {
                assertEquals(startValue + j, block.getData().elementAt(j));
            }
        }
        assertEquals(sizeof, array.sizeof());
    }

    private int writeArray(int numItems, byte startValue, short[] size)
        throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);

        os.writeInt(numItems);
        for (int i = 0; i < numItems; i++)
        {
            os.writeShort(size[i]);
        }

        for (int i = 0; i < numItems; i++)
        {
            for (int j = 0; j < size[i]; j++)
            {
                os.writeByte(startValue + j);
            }
        }
        int sizeof = (int) os.getStreamPosition();
        os.close();

        return sizeof;
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    public void testParamArray1() throws Exception
    {
        short[] sizes = new short[] { 1, 3, 2 };

        int size = writeArray((short) 3, (byte) 30, sizes);
        ParamArray array = new ParamArray(fileName);
        checkArray(array, size, (short) 3, (byte) 30, sizes);

        array.write(wFileName);

        ParamArray array2 = new ParamArray(wFileName);
        checkArray(array2, size, (short) 3, (byte) 30, sizes);
        assertTrue(array.equals(array2));
    }
    
    public void testVirtualConstructor()
    {
        int size = 3;
        ParamBlock block = new ParamBlock();
        block.setSize(size);
        byte[] bytes = { 18, 28, 38 };
        ByteArray byteArray = new ByteArray(bytes, 0, 3);
        block.setData(byteArray);
        byte[] blob = DataScriptIO.write(block);
        ParamBlock block2 = DataScriptIO.read(ParamBlock.class, blob, size);
        assertEquals(block, block2);
    }
}
