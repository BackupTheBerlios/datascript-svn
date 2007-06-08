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
import datascript.runtime.io.FileBitStreamWriter;

/**
 * Test cases using the BitStruct sequence type from bits.ds.
 * @author HWellmann
 *
 */
public class BitsParserTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "BitsParserTest.data";
    private String fileName = "bitparsertest.bin";
    private File file = new File(fileName);


    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        file.delete();
    }

    /**
     * Test read() methods by encoding a BitStruct manually and decoding it
     * with the DataScript decoder
     */
    private void writeAndReadBitStruct(int a, int b, int c) throws Exception
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
        
        bits.write(wFileName);

        BitStruct bits2 = new BitStruct(wFileName);
        assertEquals(a, bits2.getA());
        assertEquals(b, bits2.getB());
        assertEquals(c, bits2.getC());
        assertEquals(2, bits2.sizeof());
    }

    /**
     * Test read() and write() methods, by creating a BitStruct in a file
     * using write() methods of generated code and then decoding the file
     * using the read() methods.
     */
    private void encodeAndDecode(int a, int b, int c) throws Exception
    {
        BitStruct bs = new BitStruct((byte)a, (short)b, (byte)c);
        FileBitStreamWriter os = new FileBitStreamWriter(fileName);
        bs.write(os);
        os.close();

        bits.BitStruct bits = new bits.BitStruct(fileName);
        assertEquals(a, bits.getA());
        assertEquals(b, bits.getB());
        assertEquals(c, bits.getC());
        assertEquals(2, bits.sizeof());
        
        bits.write(wFileName);

        BitStruct bits2 = new BitStruct(wFileName);
        assertEquals(a, bits2.getA());
        assertEquals(b, bits2.getB());
        assertEquals(c, bits2.getC());
        assertEquals(2, bits2.sizeof());
    }

    /**
     * Test read() and write() methods as above, but using by ByteArray in
     * memory rather than a file.
     */
    private void encodeAndDecodeInMemory(int a, int b, int c) throws Exception
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
        
        bits.write(wFileName);

        BitStruct bits2 = new BitStruct(wFileName);
        assertEquals(a, bits2.getA());
        assertEquals(b, bits2.getB());
        assertEquals(c, bits2.getC());
        assertEquals(2, bits2.sizeof());
    }

    public void testBitStruct1() throws Exception
    {
        writeAndReadBitStruct(10, 200, 12);
    }

    public void testBitStruct2() throws Exception
    {
        writeAndReadBitStruct(15, 255, 15);
    }

    public void testBitStruct3() throws Exception
    {
        writeAndReadBitStruct(2, 3, 4);
    }

    public void testEncodeAndDecode1() throws Exception
    {
        encodeAndDecode(10, 200, 12);
    }

    public void testEncodeAndDecode2() throws Exception
    {
        encodeAndDecode(15, 255, 15);
    }

    public void testEncodeAndDecode3() throws Exception
    {
        encodeAndDecode(2, 3, 4);
    }

    public void testEncodeAndDecodeInMemory1() throws Exception
    {
        encodeAndDecodeInMemory(10, 200, 12);
    }

    public void testEncodeAndDecodeInMemory2() throws Exception
    {
        encodeAndDecodeInMemory(15, 255, 15);
    }

    public void testEncodeAndDecodeInMemory3() throws Exception
    {
        encodeAndDecodeInMemory(2, 3, 4);
    }
}
