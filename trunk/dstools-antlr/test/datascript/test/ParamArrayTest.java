/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.ParamArray;
import bits.ParamBlock;
import datascript.runtime.array.UnsignedShortArray;

/**
 * @author HWellmann
 *
 */
public class ParamArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "d:\\CompoundArryWriteTest.data";
    private String fileName = "paramarraytest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public ParamArrayTest(String name)
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

    private void checkArray(ParamArray array, int sizeof, short numItems, byte startValue, short[] size)
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
        		assertEquals(startValue+j, block.getData().elementAt(j));
        	}
        }
        assertEquals(sizeof, array.sizeof());
    }
    
    private int writeArray(short numItems, byte startValue, short[] size) throws IOException
    {
    	file.delete();
        os = new FileImageOutputStream(file);

        os.writeShort(numItems);
        for (int i = 0; i < numItems; i++)
        {
            os.writeShort(size[i]);
        }
        
        for (int i = 0; i < numItems; i++)
        {
        	for (int j = 0; j < size[i]; j++)
        	{
        		os.writeByte(startValue+j);
        	}
        }
        int sizeof = (int)os.getStreamPosition();
        os.close();

        return sizeof;
    }
    
    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    public  void testParamArray1() throws IOException
    {
        short[] sizes = new short[] {1, 3, 2};
        
    	int size = writeArray((short) 3, (byte) 30, sizes);
        ParamArray array = new ParamArray(fileName);
        checkArray(array, size, (short) 3, (byte) 30, sizes);
        
        array.write(wFileName);

        ParamArray array2 = new ParamArray(wFileName);
        checkArray(array2, size, (short) 3, (byte) 30, sizes);
    }
}
