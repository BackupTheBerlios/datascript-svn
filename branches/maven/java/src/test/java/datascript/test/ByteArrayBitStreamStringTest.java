/**
 * 
 */
package datascript.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import junit.framework.TestCase;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;

/**
 * @author HWellmann
 *
 */
public class ByteArrayBitStreamStringTest extends TestCase
{
    private ByteArrayBitStreamReader in;

    @Override
    protected void tearDown()
    {
        try
        {
            if (in != null)
                in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void testReadStrings() throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream os = new PrintStream(baos, false, "UTF-8");
        os.print("HAMBURG");
        os.write(0);
        os.print("MÜNCHEN");
        os.write(0);
        os.print("KÖLN");
        os.write(0);
        os.close();
        in = new ByteArrayBitStreamReader(baos.toByteArray());

        String hh = in.readString();
        assertEquals("HAMBURG", hh);
        
        String m = in.readString();
        assertEquals("MÜNCHEN", m);

        String k = in.readString();
        assertEquals("KÖLN", k);
    }
    
    private void writeRdsAndReadJdk(String s1, String s2, String s3) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        Reader fileReader = new InputStreamReader(new ByteArrayInputStream(writer.toByteArray()), "UTF-8");
        char[] buffer = new char[80];
        int numChars = fileReader.read(buffer);
        fileReader.close();
        
        String in = new String(buffer, 0, numChars);
        assertEquals(s1 + '\0' + s2 + '\0' + s3 + '\0', in);
    }

    private void writeRdsAndReadRds(String s1, String s2, String s3) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        in = new ByteArrayBitStreamReader(writer.toByteArray());
        String hh = in.readString();
        assertEquals(s1, hh);
        
        String m = in.readString();
        assertEquals(s2, m);

        String k = in.readString();
        assertEquals(s3, k);
    }

    private void writeRdsAndReadRdsUnaligned(String s1, String s2, String s3) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(7, 4);
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        in = new ByteArrayBitStreamReader(writer.toByteArray());
        long b = in.readBits(4);
        assertEquals(7, b);
        
        String hh = in.readString();
        assertEquals(s1, hh);
        
        String m = in.readString();
        assertEquals(s2, m);

        String k = in.readString();
        assertEquals(s3, k);
    }

     

    
    
    public void testWwriteRdsAndReadJdk() throws IOException
    {
        writeRdsAndReadJdk("HAMBURG", "MUNCHEN", "KOLN");
    }


    public void testWriteRdsAndReadJdkUmlaut() throws IOException
    {
        writeRdsAndReadJdk("HAMBURG", "MÜNCHEN", "KÖLN");
    }

    public void testWriteRdsAndReadRds() throws IOException
    {
        writeRdsAndReadRds("HAMBURG", "MUNCHEN", "KOLN");        
    }

    public void testWriteRdsAndReadRdsUmlaut() throws IOException
    {
        writeRdsAndReadRds("HAMBURG", "MÜNCHEN", "KÖLN");        
    }

    public void testWriteAndReadUnaligned() throws IOException
    {
        writeRdsAndReadRdsUnaligned("HAMBURG", "MUNCHEN", "KOLN");        
    }

    public void testWriteAndReadUnalignedUmlaut() throws IOException
    {
        writeRdsAndReadRdsUnaligned("HAMBURG", "MÜNCHEN", "KÖLN");        
    }

}
