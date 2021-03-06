/**
 * 
 */
package datascript.test;

import java.io.File;

import junit.framework.TestCase;
import datascript.runtime.BitStreamReader;
import datascript.runtime.BitStreamWriter;

/**
 * @author HWellmann
 *
 */
public class BitStreamWriterTest extends TestCase
{
    private BitStreamWriter os;
    private String fileName = "bitstreamwritertest.bin";
    private File file = new File(fileName);
    private BitStreamReader in;

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public BitStreamWriterTest(String name)
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
    }

    public void test1() throws Exception
    {
        os = new BitStreamWriter(fileName);
        os.writeBits(6, 4);
        os.writeBits(7, 4);
        os.writeBits(8, 4);
        os.writeBits(9, 4);
        os.writeBits(1, 4);
        os.writeBits(2, 4);
        os.writeBits(3, 4);
        os.writeBits(4, 4);
        os.writeBits(12, 4);
        os.writeBits(13, 4);
        os.writeBits(14, 4);
        os.writeBits(15, 4);
        os.close();
        in = new BitStreamReader(fileName);
        long a = 0;
        a = in.readBits(4);
        assertEquals(a, 6);
        a = in.readBits(4);
        assertEquals(a, 7);
        a = in.readBits(4);
        assertEquals(a, 8);
        a = in.readBits(4);
        assertEquals(a, 9);
        a = in.readBits(4);

        assertEquals(a, 1);
        a = in.readBits(4);
        assertEquals(a, 2);
        a = in.readBits(4);
        assertEquals(a, 3);
        a = in.readBits(4);
        assertEquals(a, 4);

        a = in.readBits(4);
        assertEquals(a, 12);
        a = in.readBits(4);
        assertEquals(a, 13);
        a = in.readBits(4);
        assertEquals(a, 14);
        a = in.readBits(4);
        assertEquals(a, 15);

        in.close();
        file.delete();        
    }

    public void test2() throws Exception
    {
        os = new BitStreamWriter(fileName);
        os.writeBits(6, 4);
        os.writeByte(0x78);
        os.writeByte(0x91);
        os.writeByte(0x23);
        os.writeByte(0x4C);
        os.writeByte(0xDE);
        os.writeBits(0xF, 4);
        os.close();
        in = new BitStreamReader(fileName);
        long a = 0;
        a = in.readBits(4);
        assertEquals(a, 6);
        a = in.readBits(4);
        assertEquals(a, 7);
        a = in.readBits(4);
        assertEquals(a, 8);
        a = in.readBits(4);
        assertEquals(a, 9);
        a = in.readBits(4);

        assertEquals(a, 1);
        a = in.readBits(4);
        assertEquals(a, 2);
        a = in.readBits(4);
        assertEquals(a, 3);
        a = in.readBits(4);
        assertEquals(a, 4);

        a = in.readBits(4);
        assertEquals(a, 12);
        a = in.readBits(4);
        assertEquals(a, 13);
        a = in.readBits(4);
        assertEquals(a, 14);
        a = in.readBits(4);
        assertEquals(a, 15);

        in.close();
        file.delete();        
    }

    public void test3() throws Exception
    {
        os = new BitStreamWriter(fileName);
        os.writeBits(6, 4);
        os.writeShort(0x7891);
        os.writeShort(0x234C);
        os.writeBits(0xD, 4);
        os.writeByte(0xEF);
        os.close();
        in = new BitStreamReader(fileName);
        long a = 0;
        a = in.readBits(4);
        assertEquals(a, 6);
        a = in.readBits(4);
        assertEquals(a, 7);
        a = in.readBits(4);
        assertEquals(a, 8);
        a = in.readBits(4);
        assertEquals(a, 9);
        a = in.readBits(4);

        assertEquals(a, 1);
        a = in.readBits(4);
        assertEquals(a, 2);
        a = in.readBits(4);
        assertEquals(a, 3);
        a = in.readBits(4);
        assertEquals(a, 4);

        a = in.readBits(4);
        assertEquals(a, 12);
        a = in.readBits(4);
        assertEquals(a, 13);
        a = in.readBits(4);
        assertEquals(a, 14);
        a = in.readBits(4);
        assertEquals(a, 15);

        in.close();
        file.delete();        
    }
}
