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
    private String wFileName = "ParamBitFieldTest2.data";
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
    	if (file.exists())
    		file.delete();
    }
    
    
    private void checkBitField(VarBitField var, int size, 
    		int numBits, int x, int y)
    {
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
            assertEquals(size, s);
        }
        catch (RuntimeException exc)
        {
            // expected exception
        }
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeBitField(int numBits, int x, int y) throws IOException
    {
    	if (file.exists())
    		file.delete();

        os = new FileImageOutputStream(file);
        os.writeBits(numBits, 5);
        os.writeBits(x, numBits);
        os.writeBits(y, numBits);
        os.writeBits(x, numBits);
        os.writeBits(y, numBits);
        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }
        
    public void testBitField1() throws IOException
    {        
        int size = writeBitField(5, 10, 20);
        VarBitField var = new VarBitField(fileName);
        checkBitField(var, size, 5, 10, 20);

        var.write(wFileName);

        VarBitField var2 = new VarBitField(wFileName);
        checkBitField(var2, size, 5, 10, 20);
        assertTrue(var.equals(var2));
    }

    public void testBitField2() throws IOException
    {
    	int size = writeBitField(5, 15, 27);
        VarBitField var = new VarBitField(fileName);
        checkBitField(var, size, 5, 15, 27);
        
        var.write(wFileName);

        VarBitField var2 = new VarBitField(wFileName);
        checkBitField(var2, size, 5, 15, 27);
        assertTrue(var.equals(var2));
    }

    public void testBitField3() throws IOException
    {
    	int size = writeBitField(10, 500, 1000);
        VarBitField var = new VarBitField(fileName);
        checkBitField(var, size, 10, 500, 1000);

        var.write(wFileName);

        VarBitField var2 = new VarBitField(wFileName);
        checkBitField(var2, size, 10, 500, 1000);
        assertTrue(var.equals(var2));
    }
}
