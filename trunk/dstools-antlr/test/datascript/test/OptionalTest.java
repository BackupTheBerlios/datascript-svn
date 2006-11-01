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
    
    public void testOptional1() throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(97);
        os.writeShort(1000);
        os.writeShort(2000);
        int size = (int) os.getStreamPosition();
        os.close();
        
        Optional opt = new Optional(fileName);
        assertEquals(97, opt.getTag());
        assertEquals(1000, opt.getA());
        assertEquals(2000, opt.getC());
        assertEquals(false, opt.hasIa());
        assertEquals(false, opt.hasB());
        assertEquals(size, opt.sizeof());
    }

    public void testOptional2() throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(98);
        os.writeShort(1000);
        os.writeByte(1);
        os.writeShort(40000);
        os.writeShort(2000);
        int size = (int) os.getStreamPosition();
        os.close();
        
        Optional opt = new Optional(fileName);
        assertEquals(98, opt.getTag());
        assertEquals(1000, opt.getA());
        assertEquals(true, opt.hasIa());
        assertEquals(false, opt.hasB());
        ItemA ia = opt.getIa();
        assertEquals(1, ia.getTag());
        assertEquals(40000, ia.getValue());
        assertEquals(2000, opt.getC());
        assertEquals(size, opt.sizeof());
    }

    public void testOptional3() throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(99);
        os.writeShort(1000);
        os.writeInt(123456);
        os.writeShort(2000);
        int size = (int) os.getStreamPosition();
        os.close();
        
        Optional opt = new Optional(fileName);
        assertEquals(99, opt.getTag());
        assertEquals(1000, opt.getA());
        assertEquals(false, opt.hasIa());
        assertEquals(true, opt.hasB());
        assertEquals(123456, opt.getB());
        assertEquals(2000, opt.getC());
        assertEquals(size, opt.sizeof());
    }
}
