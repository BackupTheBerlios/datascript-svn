/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.imageio.stream.FileImageOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import junit.framework.TestCase;
import bits.ItemA;
import bits.ItemB;
import bits.__XmlDumper;
import bits.VarArray;
import datascript.runtime.ObjectArray;

/**
 * @author HWellmann
 *
 */
public class ArrayParserTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "arrayparsertest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public ArrayParserTest(String name)
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
    private void writeArray(int total, int numA, int numB) throws IOException
    {
        short valueA = 20;
        short valueB = 1000;
        
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(total);

        for (int i = 0; i < numA; i++)
        {
            os.writeByte(1);
            os.writeShort(valueA+i);
        }
        for (int i = 0; i < numB; i++)
        {
            os.writeByte(2);
            os.writeInt(valueB+i);
        }
        os.close();

        bits.VarArray var = new bits.VarArray(fileName);
        ObjectArray a = var.getA();
        assertEquals(numA, a.length());
        for (int i = 0; i < numA ; i++)
        {
            ItemA itemA = (ItemA) a.elementAt(i);
            assertEquals(valueA+i, itemA.getValue());
        }
        ObjectArray b = var.getB();
        assertEquals(numB, b.length());
        for (int i = 0; i < numB; i++)
        {
            ItemB itemB = (ItemB) b.elementAt(i);
            assertEquals(valueB+i, itemB.getValue());
        }
        assertEquals(var.getNumItems(), a.length() + b.length());
        //dumpXmlFromVisitor(var);
    }

    private void dumpXmlFromVisitor(VarArray u)
    {
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new SAXSource(new __XmlDumper(u), new InputSource());
            Result result = new StreamResult(new OutputStreamWriter(System.out));
            t.transform(source, result);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    
    
    public void testArray1() throws IOException
    {
        writeArray(5, 2, 3);
    }

    public void testArray2() throws IOException
    {
        writeArray(5000, 2000, 3000);
    }

    public void testArray3() throws IOException
    {
        try 
        {
            writeArray(80, 20, 30);
            fail("IOException expected!");
        }
        catch (IOException exc)
        {
            // This exception is expected.
            // System.out.println("IOException caught");
        }
    }

    public void testArray4() throws IOException
    {
        try 
        {
            writeArray(44, 20, 30);
            fail("IOException expected!");
        }
        catch (IOException exc)
        {
            // This exception is expected.
            // System.out.println("IOException caught");
        }
    }
}
