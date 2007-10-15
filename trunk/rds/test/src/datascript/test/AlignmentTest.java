/**
 * 
 */


package datascript.test;


import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import alignment.Alignseq;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;



/**
 * @author HWedekind
 *
 */
public class AlignmentTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "AlignmentTest.data";
    private String fileName = "AlignmentTest.bin";
    private File file = new File(fileName);


    public AlignmentTest(String name)
    {
        super(name);
    }


    protected void tearDown() throws Exception
    {
        file.delete();
    }


    private int writeAlignment(byte u4, byte u1) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);

        os.writeBits(u4, 4);
        os.writeBits(0, 4);
        os.writeByte(24/8);
        os.writeBits(u1, 1);
        os.writeBits(0, 7);
        os.writeBits(u4, 4);
        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }


    private void checkAlignment(Alignseq as, int bitsize, byte u4, byte u1)
    {
        assertEquals(u4, as.getA());
        assertEquals(24/8, as.getLabel1());
        assertEquals(u1, as.getFlag());
        assertEquals(u4, as.getB());

        assertEquals(bitsize, as.bitsizeof());
    }


    public void testUnsigned1() throws Exception
    {
        writeAlignment((byte)15, (byte)1);
        Alignseq as = new Alignseq(fileName);
        checkAlignment(as, 28, (byte)15, (byte)1);

        as.write(wFileName);

        Alignseq as2 = new Alignseq(wFileName);
        checkAlignment(as2, 28, (byte)15, (byte)1);
        assertEquals(as, as2);
    }
    
    public void testWriteAndRead() throws Exception 
    {
        Alignseq as = new Alignseq();
        as.setA((byte)12);
        as.setFlag((byte)1);
        as.setLabel1((short)3);
        as.setB((byte)13);
        
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        as.write(writer);
        writer.close();
        
        byte[] blob = writer.toByteArray();
        
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blob);
        Alignseq as2 = new Alignseq(reader);
        
        assertEquals(12, as2.getA());
        assertEquals(1, as2.getFlag());
        assertEquals(3, as2.getLabel1());
        assertEquals(13, as2.getB());
    }

    public void testIncorrectOffset() 
    {
        Alignseq as = new Alignseq();
        as.setA((byte)12);
        as.setFlag((byte)1);
        as.setLabel1((short)4);
        as.setB((byte)13);
        
        try
        {
            ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            as.write(writer);
            writer.close();
            
            fail("expected IOException");
        }
        catch (Exception exc)
        {
            assertTrue(exc instanceof IOException);
            assertEquals("wrong offset for field 'b'", exc.getMessage());
        }
    }
}
