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
import bits.__XmlDumper;

/**
 * @author HWellmann
 *
 */
public class UnsignedTest extends TestCase
{
    private FileImageOutputStream os;

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

    private void dumpXmlFromVisitor(Unsigned u)
    {
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new SAXSource(new __XmlDumper(u), new InputSource());
            Result result = new StreamResult(new OutputStreamWriter(System.out));
            t.transform(source, result);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private void writeAndReadUnsigned(int u8, int u16, long u32, long u64)
            throws IOException
    {

        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(u8, 8);
        os.writeBits(u16, 16);
        os.writeBits(u32, 32);
        os.writeBits(u64, 64);
        os.close();

        bits.Unsigned u = new bits.Unsigned(fileName);
        assertEquals(u8, u.getU8());
        assertEquals(u16, u.getU16());
        assertEquals(u32, u.getU32());
        assertTrue(BigInteger.valueOf(u64).equals(u.getU64()));
        assertEquals(15, u.sizeof());

        //dumpXmlFromVisitor(u);
    }

    public void testUnsigned1() throws IOException
    {
        writeAndReadUnsigned(10, 200, 12, 10000);
    }

    public void testUnsigned2() throws IOException
    {
        writeAndReadUnsigned(200, 40000, 0xCAFECAFEL, 10000);
    }

    public void testUnsigned3() throws IOException
    {
        writeAndReadUnsigned(2, 3, 4, 10000);
    }
}
