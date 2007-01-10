package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import datascript.runtime.array.UnsignedByteArray;

import junit.framework.TestCase;

import bits.RecursiveStructure;
import bits.RecursiveData;


public class RecursiveStructureTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "d:\\RecursiveStructureTest.data";
    private String fileName = "d:\\recursivestructur.bin";
    private File file = new File(fileName);


    protected void tearDown() throws Exception
    {
        super.tearDown();
        file.delete();
    }

    /*
RecursiveStructure
{
    uint8   count;
    RecursiveData(count) theElement if count > 0;
};

RecursiveData(uint8 byteCount)
{
    uint8   dataBytes[byteCount]    : byteCount > 0;
    uint8   blockTerminator;

    RecursiveData(blockTerminator)  nextData    if blockTerminator > 0;
};
     */
    private int writeData(int byteCount, int blockCount) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);

        for (int b = 0; b < blockCount; b++)
        {
            os.writeByte(byteCount);
            for (int i = 0; i < byteCount; i++)
            {
                os.writeByte(i);
            }
        }
        os.writeByte(0);

        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }

    private int checkRecursive(RecursiveData rd, int byteCount)
    {
        UnsignedByteArray uba = rd.getDataBytes();
        assertEquals(byteCount, uba.sizeof());
        short bt = rd.getBlockTerminator();
        if (bt > 0)
            return 1 + checkRecursive(rd.getNextData(), byteCount);
        return 1;
    }
    
    private void checkData(RecursiveStructure rs, int size, int byteCount, int blockCount)
    {
        assertEquals(size, rs.sizeof());
        assertEquals(byteCount, rs.getCount());
        if (rs.getCount() > 0)
        {
            int bc = checkRecursive(rs.getTheElement(), byteCount);
            assertEquals(blockCount, bc);
        }
    }

    public void testRecursiveStructure1() throws IOException
    {
        int size = writeData(0x38, 3);
        RecursiveStructure rs = new RecursiveStructure(fileName);
        checkData(rs, size, 0x38, 3);

        rs.write(wFileName);

        RecursiveStructure rs2 = new RecursiveStructure(wFileName);
        checkData(rs2, size, 0x38, 3);
    }

    public void testRecursiveStructure2() throws IOException
    {
        int size = writeData(255, 5);
        RecursiveStructure rs = new RecursiveStructure(fileName);
        checkData(rs, size, 255, 5);

        rs.write(wFileName);

        RecursiveStructure rs2 = new RecursiveStructure(wFileName);
        checkData(rs2, size, 255, 5);
    }
}
