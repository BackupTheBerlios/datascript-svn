/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.Block;
import bits.BlockData;
import bits.BlockHeader;
import bits.BlockType;
import bits.Blocks;
import datascript.runtime.ByteArray;

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
        int magic = 0x0EADBEEF;
        os = new FileImageOutputStream(file);
        os.writeShort(numBlocks);

        // block 0
        os.writeByte(sorted);
        os.writeShort(sizes[0]);
        os.writeInt(magic);
        writeBytes(sizes[0]);

        // block 1
        os.writeByte(unsorted);
        os.writeShort(sizes[1]);
        os.writeInt(magic);
        writeBytes(sizes[1]);

        // block 2
        os.writeByte(sorted);
        os.writeShort(sizes[2]);
        os.writeInt(magic);
        writeBytes(sizes[2]);
        int sizeof = (int) os.getStreamPosition();
        os.close();

        Blocks blocks = new Blocks(fileName);
        assertEquals(numBlocks, blocks.getNumBlocks());

        for (int i = 0; i < numBlocks; i++)
        {
            Block block = blocks.getBlocks().elementAt(i);
            BlockHeader header = block.getHeader();
            BlockType type = (i % 2 == 0) ? BlockType.SORTED
                    : BlockType.UNSORTED;

            assertEquals(type, header.getType());
            assertEquals(sizes[i], header.getSize());

            BlockData data = block.getData();
            assertEquals(magic, data.getMagic());

            ByteArray bytes = data.getBytes();
            for (int j = 0; j < sizes[i]; j++)
            {
                assertEquals(11 + j, bytes.elementAt(j));
            }
        }
        assertEquals(sizeof, blocks.sizeof());
    }
}
