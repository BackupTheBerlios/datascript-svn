/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import datascript.runtime.BitStreamReader;

/**
 * @author HWellmann
 *
 */
public class BitsParserTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "bitparsertest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public BitsParserTest(String name)
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
    private void writeAndReadBitStruct(int a, int b, int c) throws IOException
    {
        
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(a, 4);
        os.writeBits(b, 8);
        os.writeBits(c, 4);
        os.close();

        bits.BitStruct bits = new bits.BitStruct(fileName);
        assertEquals(a, bits.getA());
        assertEquals(b, bits.getB());
        assertEquals(c, bits.getC());
        assertEquals(2, bits.sizeof());
    }

    public void testBitStruct1() throws IOException
    {
        writeAndReadBitStruct(10, 200, 12);
    }

    public void testBitStruct2() throws IOException
    {
        writeAndReadBitStruct(15, 255, 15);
    }

    public void testBitStruct3() throws IOException
    {
        writeAndReadBitStruct(2, 3, 4);
    }
}
