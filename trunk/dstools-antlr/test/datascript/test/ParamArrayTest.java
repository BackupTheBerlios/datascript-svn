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

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    public  void testParamArray1() throws IOException
    {
        os = new FileImageOutputStream(file);
        short numItems = 3;
        short[] size = new short[] {1, 3, 2};
        byte startValue = 10;

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

        ParamArray array = new ParamArray(fileName);
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
}
