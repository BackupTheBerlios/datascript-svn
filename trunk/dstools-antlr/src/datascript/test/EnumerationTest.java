/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.Colour;
import bits.Dimension;
import bits.Enums;
/**
 * @author HWellmann
 *
 */
public class EnumerationTest extends TestCase
{
    private FileImageOutputStream os;

    private String fileName = "enumerationtest.bin";

    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public EnumerationTest(String name)
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
    private void writeEnums(Colour c1, Colour c2, Dimension d1, Dimension d2)
            throws IOException
    {

        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(c1.getValue(), 16);
        os.writeBits(c2.getValue(), 16);
        os.writeBits(d1.getValue(), 8);
        os.writeBits(d2.getValue(), 8);
        os.close();

        Enums e = new Enums(fileName);
        assertEquals(c1, e.getColour1());
        assertEquals(c2, e.getColour2());
        assertEquals(d1, e.getDim1());
        assertEquals(d2, e.getDim2());
    }

    public void testUnsigned1() throws IOException
    {
        writeEnums(Colour.RED, Colour.BLUE, Dimension.HEIGHT, Dimension.LENGTH);
    }

    public void testUnsigned2() throws IOException
    {
        writeEnums(Colour.GREEN, Colour.YELLOW, Dimension.WIDTH, Dimension.HEIGHT);
    }

    public void testUnsigned3() throws IOException
    {
        writeEnums(Colour.YELLOW, Colour.BLUE, Dimension.HEIGHT, Dimension.WIDTH);
    }
}
