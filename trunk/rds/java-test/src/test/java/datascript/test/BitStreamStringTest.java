/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import junit.framework.TestCase;
import datascript.runtime.io.FileBitStreamReader;
import datascript.runtime.io.FileBitStreamWriter;

/**
 * @author HWellmann
 *
 */
public class BitStreamStringTest extends TestCase
{
    private String fileName = "bitstreamreaderstringtest.bin";
    private File file = new File(fileName);
    private FileBitStreamReader in;

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
        file.delete();
    }

    public void testReadStrings() throws IOException
    {
        PrintStream os = new PrintStream(fileName, "UTF-8");
        os.print("HAMBURG");
        os.write(0);
        os.print("MÜNCHEN");
        os.write(0);
        os.print("KÖLN");
        os.write(0);
        os.close();
        in = new FileBitStreamReader(fileName);

        String hh = in.readString();
        assertEquals("HAMBURG", hh);
        
        String m = in.readString();
        assertEquals("MÜNCHEN", m);

        String k = in.readString();
        assertEquals("KÖLN", k);
    }
    
    private void writeRdsAndReadJdk(String s1, String s2, String s3) throws IOException
    {
        FileBitStreamWriter writer = new FileBitStreamWriter(fileName);
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        Reader fileReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        char[] buffer = new char[80];
        int numChars = fileReader.read(buffer);
        fileReader.close();
        
        String in = new String(buffer, 0, numChars);
        assertEquals(s1 + '\0' + s2 + '\0' + s3 + '\0', in);
    }

    private void writeRdsAndReadRds(String s1, String s2, String s3) throws IOException
    {
        FileBitStreamWriter writer = new FileBitStreamWriter(fileName);
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        in = new FileBitStreamReader(fileName);
        String hh = in.readString();
        assertEquals(s1, hh);
        
        String m = in.readString();
        assertEquals(s2, m);

        String k = in.readString();
        assertEquals(s3, k);
    }

    private void writeRdsAndReadRdsUnaligned(String s1, String s2, String s3) throws IOException
    {
        FileBitStreamWriter writer = new FileBitStreamWriter(fileName);
        writer.writeBits(7, 4);
        writer.writeString(s1);
        writer.writeString(s2);
        writer.writeString(s3);
        writer.close();
        
        in = new FileBitStreamReader(fileName);
        long b = in.readBits(4);        
        assertEquals(7, b);
        
        String hh = in.readString();
        assertEquals(s1, hh);
        
        String m = in.readString();
        assertEquals(s2, m);

        String k = in.readString();
        assertEquals(s3, k);
    }


    public void testWriteRdsAndReadJdk() throws IOException
    {
        writeRdsAndReadJdk("HAMBURG", "BERLIN", "FRANKFURT");
    }

    public void testWriteRdsAndReadJdkUmlaut() throws IOException
    {
        writeRdsAndReadJdk("HAMBURG", "M�NCHEN", "K�LN");
    }

    public void testWriteRdsAndReadRds() throws IOException
    {
        writeRdsAndReadRds("HAMBURG", "BERLIN", "FRANKFURT");        
    }

    public void testWriteAndReadRdsUmlaut() throws IOException
    {
        writeRdsAndReadRds("HAMBURG", "M�NCHEN", "K�LN");        
    }

    public void testWriteAndReadUnaligned() throws IOException
    {
        writeRdsAndReadRdsUnaligned("HAMBURG", "BERLIN", "FRANKFURT");        
    }

    public void testWriteAndReadUnalignedUmlaut() throws IOException
    {
        writeRdsAndReadRdsUnaligned("HAMBURG", "M�NCHEN", "K�LN");        
    }
}
