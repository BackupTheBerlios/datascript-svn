/**
 * 
 */
package datascript.test;

import java.io.File;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.GlobalLabelSeq;
import bits.Header;
import bits.ItemA;
import bits.LabelledType;

/**
 * @author HWellmann
 *
 */
public class LabelTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "labeltest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public LabelTest(String name)
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
    public  void testLabel1() throws Exception
    {
        os = new FileImageOutputStream(file);
        short numItems = 3;
        int magic = 0xFFFF;
        int x = 0x076543231;

        os.writeShort(magic);
        os.writeShort(numItems);
        int dataOffset = 6+4*numItems;
        int globalOffset = 2 + dataOffset + 3*numItems;
        os.writeShort(dataOffset);
        os.writeShort(globalOffset);
        
        for (int i = 0; i < numItems; i++)
        {
            os.writeShort(10+i);
            os.writeShort(20+i);
        }
        
        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(1);
            os.writeShort(30+i);
        }
        
        os.writeInt(x);
        int size = (int) os.getStreamPosition();
        os.close();

        GlobalLabelSeq seq = new GlobalLabelSeq(fileName);
        assertEquals(magic, seq.getMagic());
        LabelledType lt = seq.getLt();
        assertEquals(numItems, lt.getNumItems());
        assertEquals(dataOffset, lt.getDataOffset());
        assertEquals(globalOffset, lt.getGlobalOffset());
        for (int i = 0; i < numItems; i++)
        {
        	Header header = lt.getHeaders().elementAt(i);
            assertEquals(10+i, header.getLen());
            assertEquals(20+i, header.getC());
        }
        for (int i = 0; i < numItems; i++)
        {
        	ItemA a = lt.getA().elementAt(i);
        	assertEquals(30+i, a.getValue());
        }
        assertEquals(x, lt.getX());
        assertEquals(size, seq.sizeof());
    }
}
