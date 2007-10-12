/**
 * 
 */


package datascript.test;


import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import alignment.*;



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


    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public AlignmentTest(String name)
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
        int size = writeAlignment((byte)15, (byte)1);
        Alignseq as = new Alignseq(fileName);
        checkAlignment(as, 28, (byte)15, (byte)1);

        as.write(wFileName);

        Alignseq as2 = new Alignseq(wFileName);
        checkAlignment(as2, 28, (byte)15, (byte)1);
        assert(as.equals(as2));
    }
}
