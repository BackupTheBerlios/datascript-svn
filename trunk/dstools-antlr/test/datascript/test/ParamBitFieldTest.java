/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.Coord;
import bits.VarBitField;
import datascript.runtime.array.BitFieldArray;

/**
 * @author HWellmann
 *
 */
public class ParamBitFieldTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "parambitfieldtest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public ParamBitFieldTest(String name)
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
    private void writeBitField(int numBits, int x, int y) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeBits(numBits, 5);
        os.writeBits(x, numBits);
        os.writeBits(y, numBits);
        os.writeBits(x, numBits);
        os.writeBits(y, numBits);
        int size = (int) os.getStreamPosition();
        os.close();

        VarBitField var = new VarBitField(fileName);
        assertEquals(numBits, var.getNumBits());
        Coord c = var.getCoord();
        assertEquals(x, c.getX().intValue());
        assertEquals(y, c.getY().intValue());
        BitFieldArray array = c.getArray();
        assertEquals(x, array.elementAt(0).intValue());
        assertEquals(y, array.elementAt(1).intValue());
        try
        { 
            int s = var.sizeof();
            fail("RuntimeException expected");
        }
        catch (RuntimeException exc)
        {
            // expected exception
        }
    }
        
    public void testBitField1() throws IOException
    {
        writeBitField(5, 10, 20);
    }

    public void testBitField2() throws IOException
    {
        writeBitField(5, 15, 27);
    }

    public void testBitField3() throws IOException
    {
        writeBitField(10, 500, 1000);
    }
}
