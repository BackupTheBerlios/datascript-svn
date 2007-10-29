/**
 * 
 */


package datascript.test;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import alignment.Alignseq;
import alignment.foo;
import datascript.runtime.array.ObjectArray;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;



/**
 * @author HWedekind
 * 
 * This test case is for testing alignment and automatic label calculations
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

        os.writeBits(u4, 4);        // member "a"
        os.writeBits(0, 4);         // 4 bit alignment
        os.writeByte(40/8);         // member "label1"
        os.writeBit(u1);            // member "flag"
        os.writeBits(0, 7);         // 7 bit alignment
        os.writeBits(0x55AA, 16);   // two elements for member "f"
        os.writeBits(u4, 4);        // member "b"

        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }


    private void checkAlignment(Alignseq as, int bitsize, byte u4, byte u1)
    {
        assertEquals(u4, as.getA());
        assertEquals(40/8, as.getLabel1());
        assertEquals(u1, as.getFlag());
        ObjectArray<foo> f = as.getF();
        assertEquals(2, f.length());
        assertEquals(0x55, f.elementAt(0).getF());
        assertEquals(0xAA, f.elementAt(1).getF());
        assertEquals(u4, as.getB());

        assertEquals(bitsize, as.bitsizeof());
    }


    public void testUnsigned1() throws Exception
    {
        writeAlignment((byte)15, (byte)1);
        Alignseq as = new Alignseq(fileName);
        checkAlignment(as, 44, (byte)15, (byte)1);

        as.write(wFileName);

        Alignseq as2 = new Alignseq(wFileName);
        checkAlignment(as2, 44, (byte)15, (byte)1);
        assertEquals(as, as2);
    }
    

    public void testWriteAndRead() throws Exception 
    {
        Alignseq as = new Alignseq();
        as.setA((byte)12);
        as.setLabel1((short)(40/8));
        as.setFlag((byte)1);
        List<foo> lf = new ArrayList<foo>(2);
        foo f = new foo((short)0x55);
        lf.add(f);
        f = new foo((short)0xAA);
        lf.add(f);
        ObjectArray<foo> fa = new ObjectArray<foo>(lf);
        as.setF(fa);
        as.setB((byte)13);
        
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        as.write(writer);
        writer.close();
        
        byte[] blob = writer.toByteArray();
        
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blob);
        Alignseq as2 = new Alignseq(reader);
        
        assertEquals(12, as2.getA());
        assertEquals(40/8, as2.getLabel1());
        assertEquals(1, as2.getFlag());
        ObjectArray<foo> fa2 = as.getF();
        assertEquals(2, fa2.length());
        assertEquals(0x55, fa2.elementAt(0).getF());
        assertEquals(0xAA, fa2.elementAt(1).getF());
        assertEquals(13, as2.getB());
    }


    public void testIncorrectOffset() 
    {
        short labelOffset = (short)(48/8);

        Alignseq as = new Alignseq();
        as.setA((byte)12);
        as.setLabel1(labelOffset);
        as.setFlag((byte)1);
        List<foo> lf = new ArrayList<foo>(2);
        foo f = new foo((short)0x55);
        lf.add(f);
        f = new foo((short)0xAA);
        lf.add(f);
        ObjectArray<foo> fa = new ObjectArray<foo>(lf);
        as.setF(fa);
        as.setB((byte)13);
        
        try
        {
            assertTrue(labelOffset == as.getLabel1());
            ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            as.write(writer);
            writer.close();

            // write method corrects a wrong label offset with help of automatic
            // label calculation
            assertTrue(labelOffset != as.getLabel1());
        }
        catch (Exception exc)
        {
            assertTrue(exc instanceof IOException);
            assertEquals("wrong offset for field 'b'", exc.getMessage());
        }
    }
}
