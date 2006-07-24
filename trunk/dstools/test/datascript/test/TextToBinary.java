/**
 * 
 */
package datascript.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

/**
 * @author HWellmann
 *
 */
public class TextToBinary
{
    private String textName;
    private String binaryName;
    private BufferedReader reader;
    private FileImageOutputStream writer;
    private int lineNum;
    
    public TextToBinary(String text, String binary)
    {
        this.textName = text;
        this.binaryName = binary;
    }
    
    public void encode()
    {
        try
        {
            reader = new BufferedReader(new FileReader(textName));
            File binaryFile = new File(binaryName);
            binaryFile.delete();
            writer = new FileImageOutputStream(binaryFile);
            writer.flushBefore(0);
            lineNum = 0;
            while (true)
            {
                String line = reader.readLine();
                lineNum++;
                if (line == null)
                    break;
                
                encodeLine(line);
            }
            reader.close();
            writer.close();
            
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }
    
    private void encodeLine(String line) throws IOException
    {
        // skip comment
        int hash = line.indexOf('#');
        if (hash != -1)
        {
            line = line.substring(0, hash);
        }
        line = line.trim();
        String[] parts = line.split(":");
        if (line.length() == 0)
        {
            return;
        }
        if (parts.length != 2)
        {
            System.err.println(textName + ":" + lineNum + ": Wrong number of parts");
            return;
        }
        long value = Long.parseLong(parts[0]);
        int width  = Integer.parseInt(parts[1]);
        writeValue(value, width);
    }
    
    private void writeValue(long value, int width) throws IOException
    {
        writer.writeBits(value, width);        
    }
    
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("usage: TextToBinary <text input> <bin output>");
            System.exit(1);
        }
        
        TextToBinary ttb = new TextToBinary(args[0], args[1]);
        ttb.encode();
    }
}
