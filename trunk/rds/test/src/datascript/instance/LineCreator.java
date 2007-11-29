package datascript.instance;
import java.io.File;
import java.io.IOException;

import lines.LineGeometries;
import datascript.runtime.io.BitStreamWriter;
import datascript.runtime.io.ByteArrayBitStreamWriter;
import datascript.runtime.io.FileBitStreamReader;
import datascript.runtime.io.FileBitStreamWriter;


public class LineCreator implements Runnable
{
    private int numLines;
    private int numPoints;
    private BitStreamWriter writer;
    
    
    @Override
    public void run()
    {
        try
        {
            File file = new File("lines.dat");
            file.delete();
            writer = new FileBitStreamWriter("lines.dat");
            writer.writeInt(numLines);
            for (int i = 0; i < numLines; i++)
            {
                writeLine();
            }
            writer.close();

            FileBitStreamReader reader = new FileBitStreamReader("lines.dat");

            @SuppressWarnings("unused")
            LineGeometries geometries = new LineGeometries(reader);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private void writeLine() throws IOException
    {
        writer.writeShort(numPoints-1);
        writer.writeInt(345000);
        writer.writeInt(678000);
        for (int i = 1; i < numPoints; i++)
        {
            writer.writeShort(10 * i);
            writer.writeShort(-10 * i);
        }        
    }
    
    public byte[] getLines(int numLines, int numPoints)
    {
        try
        {
            this.numLines = numLines;
            this.numPoints = numPoints;
            ByteArrayBitStreamWriter bwriter = new ByteArrayBitStreamWriter();
            writer = bwriter;
            writer.writeInt(numLines);
            for (int i = 0; i < numLines; i++)
            {
                writeLine();
            }
            bwriter.close();
            return bwriter.toByteArray();
        }
        catch (Exception exc)
        {
            throw new RuntimeException(exc);
        }

    }

    public static void main(String[] args)
    {
        LineCreator appl = new LineCreator();
        appl.numLines = Integer.parseInt(args[0]);
        appl.numPoints = Integer.parseInt(args[1]);
        
        appl.run();
    }



}
