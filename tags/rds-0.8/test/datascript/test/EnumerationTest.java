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
import bits.DuplicateEnum;
import bits.TrafficLight;

/**
 * @author HWellmann
 *
 */
public class EnumerationTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "EnumerationTest.data";
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
    private int writeEnums(Colour c1, Colour c2, Dimension d1, Dimension d2)
            throws IOException
    {

        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(c1.getValue(), 16);
        os.writeBits(c2.getValue(), 16);
        os.writeBits(d1.getValue(), 8);
        os.writeBits(d2.getValue(), 8);
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    private void checkEnums(Enums e, int size, Colour c1, Colour c2, Dimension d1, Dimension d2)
    {
        assertEquals(c1, e.getColour1());
        assertEquals(c2, e.getColour2());
        assertEquals(d1, e.getDim1());
        assertEquals(d2, e.getDim2());
        assertEquals(size, e.sizeof());
    }

    public void testUnsigned1() throws IOException
    {
        int size = writeEnums(Colour.RED, Colour.BLUE, Dimension.HEIGHT, Dimension.LENGTH);
        Enums e = new Enums(fileName);
        checkEnums(e, size, Colour.RED, Colour.BLUE, Dimension.HEIGHT, Dimension.LENGTH);

        e.write(wFileName);
        
        Enums e2 = new Enums(wFileName);
        checkEnums(e2, size, Colour.RED, Colour.BLUE, Dimension.HEIGHT, Dimension.LENGTH);
        assertTrue(e.equals(e2));
    }

    public void testUnsigned2() throws IOException
    {
        int size = writeEnums(Colour.GREEN, Colour.YELLOW, Dimension.WIDTH, Dimension.HEIGHT);
        Enums e = new Enums(fileName);
        checkEnums(e, size, Colour.GREEN, Colour.YELLOW, Dimension.WIDTH, Dimension.HEIGHT);

        e.write(wFileName);
        
        Enums e2 = new Enums(wFileName);
        checkEnums(e2, size, Colour.GREEN, Colour.YELLOW, Dimension.WIDTH, Dimension.HEIGHT);
        assertTrue(e.equals(e2));
    }

    public void testUnsigned3() throws IOException
    {
        int size = writeEnums(Colour.YELLOW, Colour.BLUE, Dimension.HEIGHT, Dimension.WIDTH);
        Enums e = new Enums(fileName);
        checkEnums(e, size, Colour.YELLOW, Colour.BLUE, Dimension.HEIGHT, Dimension.WIDTH);

        e.write(wFileName);
        
        Enums e2 = new Enums(wFileName);
        checkEnums(e2, size, Colour.YELLOW, Colour.BLUE, Dimension.HEIGHT, Dimension.WIDTH);
        assertTrue(e.equals(e2));
    }
    
    public void testDuplicateEnum() throws IOException
    {
    	DuplicateEnum de = new DuplicateEnum();
    	de.setColour(Colour.RED);
    	de.setLight(TrafficLight.RED);
    	assertEquals(de.getColour(), Colour.RED);
    	assertEquals(de.getLight(), TrafficLight.RED);
    	assertEquals(de.sizeof(), 3);
    	
    	de.write(wFileName);
    	
    	de = new DuplicateEnum(wFileName);
    	assertEquals(de.getColour(), Colour.RED);
    	assertEquals(de.getLight(), TrafficLight.RED);
    	assertEquals(de.sizeof(), 3);    	    	
    }
}
