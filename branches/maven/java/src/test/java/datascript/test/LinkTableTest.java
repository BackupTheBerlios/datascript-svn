package datascript.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import bits.arrays.LinkTable;
import datascript.runtime.array.BitFieldArray;
import datascript.runtime.array.UnsignedByteArray;
import datascript.runtime.io.FileBitStreamWriter;

/**
 * @author HWedekind
 *
 */
public class LinkTableTest extends TestCase
{
    private FileBitStreamWriter os;
    private String wFileName = "SumTest.data";
    private String fileName = "SumTest.bin";
    private File file = new File(fileName);

    private int writeTableSequence() throws IOException
    {
        file.delete();
        os = new FileBitStreamWriter(fileName);

        os.writeBits(1, 1);
        os.writeBits(0, 1);
        os.writeBits(1, 1);
        os.writeBits(0, 1);
        os.writeBits(1, 1);
        os.writeBits(0, 1);
        os.writeBits(1, 1);
        os.writeBits(0, 1);

        for (int i = 0; i < 4; i++)
        {
            os.writeBits(i, 8);
        }
        os.writeBits(15, 4);
        
        int size = (int)os.getBitPosition();
        os.close();
        
        return size;
    }

    private void checkTableSequence(LinkTable lt, int size)
    { 
        assertEquals(size, lt.bitsizeof());

        BitFieldArray bfa = lt.getR();
        assertEquals(8, bfa.length());
        assertEquals(1, bfa.elementAt(0).byteValue());
        assertEquals(0, bfa.elementAt(1).byteValue());
        assertEquals(1, bfa.elementAt(2).byteValue());
        assertEquals(0, bfa.elementAt(3).byteValue());
        assertEquals(1, bfa.elementAt(4).byteValue());
        assertEquals(0, bfa.elementAt(5).byteValue());
        assertEquals(1, bfa.elementAt(6).byteValue());
        assertEquals(0, bfa.elementAt(7).byteValue());

        UnsignedByteArray uba = lt.getNumRefsInLevel();
        assertEquals(4, uba.length());
        for (int i = 0; i < uba.length(); i++)
        {
            assertEquals(i, uba.elementAt(i));
        }
        assertEquals(15, lt.getB().intValue());
    }

    public void testSum1() throws Exception
    {        
        int size = writeTableSequence();
        LinkTable lt = new LinkTable(fileName);
        checkTableSequence(lt, size);

        lt.write(wFileName);

        LinkTable lt2 = new LinkTable(wFileName);
        checkTableSequence(lt2, size);
        assertTrue(lt.equals(lt2));
    }
}
