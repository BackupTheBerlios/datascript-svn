/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.AnyItem;
import bits.BaseTypeUnion;
import bits.BaseTypes;
import bits.ItemA;
import bits.ItemB;
import bits.ItemC;

/**
 * @author HWellmann
 *
 */
public class UnionTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "uniontest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public UnionTest(String name)
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
    private void writeUnion(int tag, int value) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeByte(tag);
        switch (tag)
        {
            case 8:
                os.writeByte(value);
                break;
            case 16:
                os.writeShort(value);
                break;
            case 32:
                os.writeInt(value);
                break;
        }
        os.close();

        BaseTypes b = new BaseTypes(fileName);
        assertEquals(tag, b.getTag());
        BaseTypeUnion u = b.getValue();
        switch (tag)
        {
            case 8:
                assertEquals(value, u.getV8());
                break;
            case 16:
                assertEquals(value, u.getV16());
                break;
            case 32:
                assertEquals(value, u.getV32());
                break;
        }
    }

    public void testUnion1() throws IOException
    {
        writeUnion(8, 65);
    }

    public void testUnion2() throws IOException
    {
        writeUnion(16, 2000);
    }

    public void testUnion3() throws IOException
    {
        writeUnion(32, 123123);
    }
}
