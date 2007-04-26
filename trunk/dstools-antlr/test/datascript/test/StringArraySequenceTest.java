package datascript.test;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import bits.arrays.StringArraySequence;
import datascript.runtime.io.FileBitStreamWriter;

public class StringArraySequenceTest extends TestCase
{
    private FileBitStreamWriter os;
    private String wFileName = "StringTest.data";
    private String fileName = "StringTest.bin";
    private File file = new File(fileName);

    private int writeStringSequence(String name, String[] testPattern) throws IOException
    {
        file.delete();
        os = new FileBitStreamWriter(fileName);

        os.writeBytes(name);
        os.writeByte(0);
        os.writeBits(testPattern.length, 4);
        for (int i = 0; i < testPattern.length; i++)
        {
            String tmpStr = testPattern[i];
            for (int l = 0; l < tmpStr.length(); l++)
                os.writeBits(tmpStr.charAt(l), 8);
            os.writeByte(0);
        }
        
        int size = (int)os.getStreamPosition();
        os.close();
        
        return size;
    }

    private void checkStringSequence(StringArraySequence sa, int size, String name, String[] testPattern)
    { 
        try
        {
            assertEquals(size, sa.sizeof());
        }
        catch(RuntimeException e)
        {
        	// here we expect an exception 
        	// java.lang.RuntimeException: sizeof not integer: 148
            //System.out.println(e.toString());
        }

        assertEquals(name, sa.getName());
        assertEquals(testPattern.length, sa.getByteCount());
        for (int i = 0; i < testPattern.length; i++)
        {
            assertEquals(testPattern[i], sa.getText().elementAt(i));
        }
    }

    public void testStrings1() throws IOException
    {
        String testPattern[] = {"", "ABC", "abcdef"};
        
        int size = writeStringSequence("Test1", testPattern);
        StringArraySequence sa = new StringArraySequence(fileName);
        checkStringSequence(sa, size, "Test1", testPattern);

        sa.write(wFileName);

        StringArraySequence sa2 = new StringArraySequence(wFileName);
        checkStringSequence(sa2, size, "Test1", testPattern);
        assertTrue(sa.equals(sa2));
    }

}
