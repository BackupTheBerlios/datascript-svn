/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.CompoundArray;
import bits.Header;
import bits.Inner;
import bits.ItemA;
import bits.ItemB;
import bits.Outer;
import datascript.runtime.ObjectArray;
import datascript.runtime.ShortArray;

/**
 * @author HWellmann
 *
 */
public class TypeInstantiationTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "containment.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public TypeInstantiationTest(String name)
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

    private void writeBytes(int numBytes) throws IOException
    {
        for (int i = 0; i < numBytes; i++)
        {
            os.writeByte(11+i);
        }
    }

    public void testInstantiation() throws IOException
    {
        short[] sizes = new short[] { 1, 3, 4 };
        short numBlocks = (short) sizes.length;
        byte unsorted = 3;
        byte sorted = 9;
        os = new FileImageOutputStream(file);
        os.writeShort(numBlocks);
        
        // block 0
        os.writeByte(sorted);
        os.writeShort(sizes[0]);
        os.writeInt(0xDEADBEEF);
        writeBytes(sizes[0]);
        
        // block 1
        os.writeByte(unsorted);
        os.writeShort(sizes[1]);
        os.writeInt(0xDEADBEEF);
        writeBytes(sizes[1]);
        
        // block 2
        os.writeByte(sorted);
        os.writeShort(sizes[2]);
        os.writeInt(0xDEADBEEF);
        writeBytes(sizes[2]);
        os.close();
        
        Blocks blocks = new Blocks(fileName);
        assertEquals(a, outer.getA());
        Header header = outer.getHeader();
        Inner  inner  = outer.getInner();
        assertEquals(length, header.getLen());
        assertEquals(c, header.getC());
        assertEquals(d, inner.getD());
        assertEquals(e, inner.getE());
        for (int i = 0; i < length; i++)
        {
            ShortArray array = inner.getList();
            assertEquals(20*i, array.elementAt(i));
        }        
    }
}
