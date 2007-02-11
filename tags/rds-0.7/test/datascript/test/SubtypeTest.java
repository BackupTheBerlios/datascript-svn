/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.Tile;

/**
 * @author HWellmann
 *
 */
public class SubtypeTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "SubtypeTest.data";
    private String fileName = "SubtypeTest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public SubtypeTest(String name)
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
    private int writeSubtype(int u16, long u32)
            throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeBits(u16, 16);
        os.writeBits(u32, 32);
        int size = (int)os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    private void checkSubtype(Tile u, int size, int u16, long u32)
    {
        assertEquals(u16, u.getTileId());
        assertEquals(u32, u.getTileData());
        
        assertEquals(size, u.sizeof());
    }

    public void testUnsigned1() throws IOException
    {
        int size = writeSubtype(200, 12);
        Tile u = new Tile(fileName);
        checkSubtype(u, size, 200, 12);

        u.write(wFileName);

        Tile u2 = new Tile(wFileName);
        checkSubtype(u2, size, 200, 12);
    }

    public void testUnsigned2() throws IOException
    {
        int size = writeSubtype(40000, 0xCAFECAFEL);
        Tile u = new Tile(fileName);
        checkSubtype(u, size, 40000, 0xCAFECAFEL);

        u.write(wFileName);

        Tile u2 = new Tile(wFileName);
        checkSubtype(u2, size, 40000, 0xCAFECAFEL);
    }

    public void testUnsigned3() throws IOException
    {
        int size = writeSubtype(4, 10000);
        Tile u = new Tile(fileName);
        checkSubtype(u, size, 4, 10000);

        u.write(wFileName);

        Tile u2 = new Tile(wFileName);
        checkSubtype(u2, size, 4, 10000);
    }
}
