/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.BitStruct;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;

/**
 * @author HWellmann
 *
 */
public class BitsParserTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "bitparsertest.bin";
    private File file = new File(fileName);

    public BitsParserTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        file.delete();
    }

    private void writeAndReadBitStruct(int a, int b, int c) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeBits(a, 4);
        os.writeBits(b, 8);
        os.writeBits(c, 4);
        os.close();

        BitStruct bits = new BitStruct(fileName);
        assertEquals(a, bits.getA());
        assertEquals(b, bits.getB());
        assertEquals(c, bits.getC());
        assertEquals(2, bits.sizeof());
    }

    private void encodeAndDecode(int a, int b, int c) throws IOException
    {
        BitStruct bs = new BitStruct((byte)a, (short)b, (byte)c);
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

    private void encodeAndDecodeInMemory(int a, int b, int c) throws IOException
    {
        BitStruct bs = new BitStruct((byte)a, (short)b, (byte)c);
        ByteArrayBitStreamWriter os = new ByteArrayBitStreamWriter();
        bs.write(os);
        os.close();
        
        BitStruct bits = new BitStruct(new ByteArrayBitStreamReader(os.toByteArray()));
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

    public void testEncodeAndDecode1() throws IOException
    {
        encodeAndDecode(10, 200, 12);
    }

    public void testEncodeAndDecode2() throws IOException
    {
        encodeAndDecode(15, 255, 15);
    }

    public void testEncodeAndDecode3() throws IOException
    {
        encodeAndDecode(2, 3, 4);
    }

    public void testEncodeAndDecodeInMemory1() throws IOException
    {
        encodeAndDecodeInMemory(10, 200, 12);
    }

    public void testEncodeAndDecodeInMemory2() throws IOException
    {
        encodeAndDecodeInMemory(15, 255, 15);
    }

    public void testEncodeAndDecodeInMemory3() throws IOException
    {
        encodeAndDecodeInMemory(2, 3, 4);
    }
}
