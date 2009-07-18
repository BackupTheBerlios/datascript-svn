/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;
import datascript.runtime.io.FileBitStreamReader;

/**
 * @author HWellmann
 *
 */
public class BitStreamReaderTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "bitstreamreadertest.bin";
    private File file = new File(fileName);
    private FileBitStreamReader in;

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public BitStreamReaderTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        //super.setUp();
        os = new FileImageOutputStream(file);
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
        in = new FileBitStreamReader(fileName);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        //super.tearDown();
        in.close();
        file.delete();
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    public void testReadByte() throws IOException
    {
        byte b;
        long pos;
        long bytePos;
        
        b = in.readByte();
        assertTrue(b == 6*16+7);
        b = in.readByte();
        assertTrue(b == 8*16+9 - 256);
        pos = in.getBitPosition();
        assertTrue(pos == 16);
        bytePos = in.getStreamPosition();
        assertTrue(bytePos == 2);

        in.setBitPosition(0);
    
        b = in.readByte();
        assertTrue(b == 6*16+7);
        b = in.readByte();
        assertTrue(b == 8*16+9 - 256);
        pos = in.getBitPosition();
        assertTrue(pos == 16);
        bytePos = in.getStreamPosition();
        assertTrue(bytePos == 2);
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readUnsignedByte()'
     */
    public void testReadUnsignedByte() throws IOException
    {
        int b = in.readByte();
        assertTrue(b == 6*16+7);
        b = in.readByte();
        assertTrue(b == 8*16+9 - 256);
        long pos = in.getBitPosition();
        assertTrue(pos == 16);
        long bytePos = in.getStreamPosition();
        assertTrue(bytePos == 2);

        in.setBitPosition(0);

        b = in.readByte();
        assertTrue(b == 6*16+7);
        b = in.readByte();
        assertTrue(b == 8*16+9 - 256);
        pos = in.getBitPosition();
        assertTrue(pos == 16);
        bytePos = in.getStreamPosition();
        assertTrue(bytePos == 2);
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readUnsignedByte()'
     */
    public void testReadUnsignedInt1() throws IOException
    {
        short uint8 = (short) in.readUnsignedByte();
        assertEquals(uint8, 0x67);
        long uint32 = in.readUnsignedInt();
        assertEquals(uint32, 0x891234CDL);
    }

    public void testReadUnsignedShort() throws IOException
    {
        short uint8 = (short) in.readUnsignedByte();
        assertEquals(uint8, 0x67);
        int uint16 = in.readUnsignedShort();
        assertEquals(uint16, 0x8912);
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.BitStreamReader(String)'
     */
    public void testBitStreamReader() throws IOException
    {
        long v = in.readBits(2);
        assertTrue(v == 6 >> 2);
        assertTrue(in.getBitPosition() == 2);
        assertTrue(in.getStreamPosition() == 0);
        v = in.readBits(2);
        assertTrue(v == (6 & 0x03));
        assertTrue(in.getBitPosition() == 4);
        assertTrue(in.getStreamPosition() == 0);
        v = in.readBits(2);
        assertTrue(v == 7 >> 2);
        assertTrue(in.getBitPosition() == 6);
        assertTrue(in.getStreamPosition() == 0);
        v = in.readBits(2);
        assertTrue(v == (7 & 0x03));
        assertTrue(in.getBitPosition() == 8);
        assertTrue(in.getStreamPosition() == 1);
        v = in.readBits(2);
        assertTrue(v == 8 >> 2);
        assertTrue(in.getBitPosition() == 10);
        assertTrue(in.getStreamPosition() == 1);
        v = in.readBits(2);
        assertTrue(v == (8 & 0x03));
        assertTrue(in.getBitPosition() == 12);
        assertTrue(in.getStreamPosition() == 1);
        v = in.readBits(2);
        assertTrue(v == 9 >> 2);
        assertTrue(in.getBitPosition() == 14);
        assertTrue(in.getStreamPosition() == 1);
        v = in.readBits(2);
        assertTrue(v == (9 & 0x03));
        assertTrue(in.getBitPosition() == 16);
        assertTrue(in.getStreamPosition() == 2);
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.getBitPosition()'
     */
    public void testReadByteNotAligned() throws IOException
    {
        long v;
        int uint8;
        v = in.readBits(4);
        assertTrue(v == 6);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == 0x78);
        assertTrue(in.getBitPosition() == 12);
        assertTrue(in.getStreamPosition() == 1);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == 0x91);
        assertTrue(in.getBitPosition() == 20);
        assertTrue(in.getStreamPosition() == 2);        
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.setBitPosition(long)'
     */
    public void testSetBitPosition() throws IOException
    {
        short s;
        short t = 0x6789;
        int uint8;
        s = in.readShort();
        assertTrue(s == t);   
        
        in.setBitPosition(0);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == 0x67);
        assertTrue(in.getStreamPosition() == 1);
        assertTrue(in.getBitPosition() == 8);
        
        in.setBitPosition(1);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == (t & 0x7F80) >> 7);
        assertTrue(in.getStreamPosition() == 1);
        assertTrue(in.getBitPosition() == 9);

        in.setBitPosition(2);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == (t & 0x3FC0) >> 6);
        assertTrue(in.getStreamPosition() == 1);
        assertTrue(in.getBitPosition() == 10);

        in.setBitPosition(3);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == (t & 0x1FE0) >> 5);
        assertTrue(in.getStreamPosition() == 1);
        assertTrue(in.getBitPosition() == 11);

        in.setBitPosition(4);
        uint8 = in.readUnsignedByte();
        assertTrue(uint8 == 0x78);
        assertTrue(in.getStreamPosition() == 1);
        assertTrue(in.getBitPosition() == 12);
    }
    
    public void testSignedBitfield1() throws IOException
    {
    	ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
    	writer.writeByte(-1);
    	writer.close();
    	byte[] blob = writer.toByteArray();
    	assertEquals(1, blob.length);
    	assertEquals(-1, blob[0]);
    	
    	ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blob);
    	long a = reader.readSignedBits(3);
    	long b = reader.readSignedBits(2);
    	long c = reader.readSignedBits(3);
    	assertEquals(-1L, a);
    	assertEquals(-1L, b);
    	assertEquals(-1L, c);
    }

    public void testSignedBitfield2() throws IOException
    {
    	ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
    	writer.writeShort(-10000);
    	writer.close();
    	byte[] blob = writer.toByteArray();
    	assertEquals(2, blob.length);
    	
    	ByteArrayBitStreamReader sReader = new ByteArrayBitStreamReader(blob);
    	long s = sReader.readSignedBits(16);
    	assertEquals(-10000L, s);
    	
    	ByteArrayBitStreamReader uReader = new ByteArrayBitStreamReader(blob);
    	long u = uReader.readBits(16);
    	assertEquals(55536L, u);
    }

    public void testSignedBitfield3() throws IOException
    {
    	ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
    	writer.writeBits(-5, 10);
    	writer.writeBits(-6, 10);
    	writer.writeBits(-7, 10);
    	writer.close();
    	byte[] blob = writer.toByteArray();
    	assertEquals(4, blob.length);
    	
    	ByteArrayBitStreamReader sReader = new ByteArrayBitStreamReader(blob);
    	long s1 = sReader.readSignedBits(10);
    	long s2 = sReader.readSignedBits(10);
    	long s3 = sReader.readSignedBits(10);
    	assertEquals(-5L, s1);
    	assertEquals(-6L, s2);
    	assertEquals(-7L, s3);
    	
    	ByteArrayBitStreamReader uReader = new ByteArrayBitStreamReader(blob);
    	long u1 = uReader.readBits(10);
    	long u2 = uReader.readBits(10);
    	long u3 = uReader.readBits(10);
    	assertEquals(1019, u1);
    	assertEquals(1018, u2);
    	assertEquals(1017, u3);
    }
}
