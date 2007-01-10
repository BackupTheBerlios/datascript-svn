/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import javax.imageio.stream.FileImageOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

import bits.Unsigned;

/**
 * @author HWellmann
 *
 */
public class UnsignedTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "d:\\UnsignedTest.data";
    private String fileName = "bitparsertest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public UnsignedTest(String name)
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
    private int writeUnsigned(int u8, int u16, long u32, long u64)
            throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(u8, 8);
        os.writeBits(u16, 16);
        os.writeBits(u32, 32);
        os.writeBits(u64, 64);
        int size = (int)os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    private void checkUnsigned(bits.Unsigned u, int size, int u8, int u16, long u32, long u64)
    {
        assertEquals(u8, u.getU8());
        assertEquals(u16, u.getU16());
        assertEquals(u32, u.getU32());
        assertTrue(BigInteger.valueOf(u64).equals(u.getU64()));
        
        assertEquals(size, u.sizeof());
    }

    public void testUnsigned1() throws IOException
    {
        int size = writeUnsigned(10, 200, 12, 10000);
        bits.Unsigned u = new bits.Unsigned(fileName);
        checkUnsigned(u, size, 10, 200, 12, 10000);

        u.write(wFileName);

        bits.Unsigned u2 = new bits.Unsigned(wFileName);
        checkUnsigned(u2, size, 10, 200, 12, 10000);
    }

    public void testUnsigned2() throws IOException
    {
        int size = writeUnsigned(200, 40000, 0xCAFECAFEL, 10000);
        bits.Unsigned u = new bits.Unsigned(fileName);
        checkUnsigned(u, size, 200, 40000, 0xCAFECAFEL, 10000);

        u.write(wFileName);

        bits.Unsigned u2 = new bits.Unsigned(wFileName);
        checkUnsigned(u2, size, 200, 40000, 0xCAFECAFEL, 10000);
    }

    public void testUnsigned3() throws IOException
    {
        int size = writeUnsigned(2, 3, 4, 10000);
        bits.Unsigned u = new bits.Unsigned(fileName);
        checkUnsigned(u, size, 2, 3, 4, 10000);

        u.write(wFileName);

        bits.Unsigned u2 = new bits.Unsigned(wFileName);
        checkUnsigned(u2, size, 2, 3, 4, 10000);
    }
}
