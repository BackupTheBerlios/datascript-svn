package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.OptionalArraySequence;
import datascript.runtime.array.ByteArray;
import datascript.runtime.array.ShortArray;

public class OptionalArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "OptionalArrayTest.data";
    private String fileName = "optionalarraytest.bin";
    private File file = new File(fileName);

    public OptionalArrayTest(String arg0)
    {
        super(arg0);
    }


    protected void setUp() throws Exception
    {
        super.setUp();
    }


    protected void tearDown() throws Exception
    {
        super.tearDown();
        file.delete();
    }

    private void checkData(OptionalArraySequence oas, int size, int count)
    {        
        ByteArray kleinesArray = oas.getKleinesArray();
        assertEquals(3, kleinesArray.length());
        for (int i = 0; i < 3; i++)
        {
            assertEquals(i+1, kleinesArray.elementAt(i));
        }

        assertEquals(count, oas.getCount());

        assertEquals(count > 0, oas.hasA());
        if (count > 0)
        {
            ShortArray a = oas.getA();
            assertEquals(count, a.length());
            for (int i = 1; i <= count; i++)
            {
                assertEquals(-i, a.elementAt(i-1));
            }
        }
        assertEquals(count == 0, oas.hasB());
        if (count == 0)
        {
            ShortArray b = oas.getB();
            assertEquals(1, b.length());
            assertEquals(0x1234, b.elementAt(0));
        }
    }
    
    private int writeData(int count) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        for (int i = 0; i < 3; i++)
        {
            os.writeByte(i+1);
        }

        os.writeShort(count);

        if (count > 0)
        {
            for (int i = 1; i <= count; i++)
            {
                os.writeShort(-i);
            }
        }
        if (count == 0)
        {
            os.writeShort(0x1234);
        }
        
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    public void testOptionalArray1() throws IOException
    {
        int size = writeData(5);
        OptionalArraySequence oas = new OptionalArraySequence(fileName);
        checkData(oas, size, 5);
        
        oas.write(wFileName);

        OptionalArraySequence oas2 = new OptionalArraySequence(wFileName);
        checkData(oas2, size, 5);
        assertTrue(oas.equals(oas2));
    }
    
    public void testOptionalArray2() throws IOException
    {
        int size = writeData(0);
        OptionalArraySequence oas = new OptionalArraySequence(fileName);
        checkData(oas, size, 0);
        
        oas.write(wFileName);

        OptionalArraySequence oas2 = new OptionalArraySequence(wFileName);
        checkData(oas2, size, 0);
        assertTrue(oas.equals(oas2));
    }
}
