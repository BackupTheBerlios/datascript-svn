/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.ItemA;
import bits.Optional;

/**
 * @author HWellmann
 *
 */
public class OptionalTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "OptionalTest.data";
    private String fileName = "containment.bin";
    private File file = new File(fileName);

    public OptionalTest(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
        file.delete();
    }

/*
Optional
{
    uint8       tag;
    uint16      a;
    uint32      b if tag == 99;
    ItemA       ia if tag == 98;
    uint16      c;
};
*/
    private void checkData(Optional opt, int size, int tag, int a, int b, ItemA ia, int c)
    {
        assertEquals(tag, opt.getTag());
        assertEquals(a, opt.getA());
        assertEquals(tag == 99, opt.hasB());
        if (tag == 99)
            assertEquals(b, opt.getB());
        assertEquals(tag == 98, opt.hasIa());
        if (tag == 98)
        {
            ItemA tmpIa = opt.getIa();
            assertEquals(ia.getTag(), tmpIa.getTag());
            assertEquals(ia.getValue(), tmpIa.getValue());
        }
        assertEquals(c, opt.getC());
        
        assertEquals(size, opt.sizeof());
    }

    private int writeData(int tag, int a, int b, ItemA ia, int c) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(tag);
        os.writeShort(a);
        if (tag == 99)
        {
            os.writeInt(b);
        }
        else if (tag == 98)
        {
            os.writeByte(ia.getTag());
            os.writeShort(ia.getValue());
        }
        os.writeShort(c);
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    public void testOptional1() throws IOException
    {
        ItemA ia = new ItemA((short)0, 0);
        
        int size = writeData(97, 1000, 0, ia, 2000);
        Optional opt = new Optional(fileName);
        checkData(opt, size, 97, 1000, 0, ia, 2000);

        opt.write(wFileName);

        Optional opt2 = new Optional(wFileName);
        checkData(opt2, size, 97, 1000, 0, ia, 2000);
        assertTrue(opt.equals(opt2));
    }

    public void testOptional2() throws IOException
    {
        ItemA ia = new ItemA((short)1, 40000);
        
        int size = writeData(98, 1000, 0, ia, 2000);        
        Optional opt = new Optional(fileName);
        checkData(opt, size, 98, 1000, 0, ia, 2000);

        opt.write(wFileName);

        Optional opt2 = new Optional(wFileName);
        checkData(opt2, size, 98, 1000, 0, ia, 2000);
        assertTrue(opt.equals(opt2));
    }

    public void testOptional3() throws IOException
    {
        ItemA ia = new ItemA((short)0, 0);
        
        int size = writeData(99, 1000, 123456, ia, 2000);        
        Optional opt = new Optional(fileName);
        checkData(opt, size, 99, 1000, 123456, ia, 2000);

        opt.write(wFileName);

        Optional opt2 = new Optional(wFileName);
        checkData(opt2, size, 99, 1000, 123456, ia, 2000);
        assertTrue(opt.equals(opt2));
    }
}
