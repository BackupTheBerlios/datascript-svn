/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import javax.imageio.stream.FileImageOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

import bits.ItemA;
import bits.ItemB;
import bits.VarArray;
import bits.__XmlDumper;
import datascript.runtime.ObjectArray;

/**
 * @author HWellmann
 *
 */
public class VariableBitFieldTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "bitfieldtest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public VariableBitFieldTest(String name)
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
    private void writeVarBitField(int numBits, long x, long y) throws IOException
    {       
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeByte(numBits);
        os.writeBits(x, numBits);
        os.writeBits(y, numBits);
        os.writeBits(0xFFFF, 16);
        os.close();

        bits.VarBitField var = new bits.VarBitField(fileName);
        short numBitsRead = var.getNumBits();
        assertEquals(numBits, numBitsRead);

        BigInteger xRead = var.getX();
        assertEquals(xRead.longValue(), x);
        
        BigInteger yRead = var.getY();
        assertEquals(yRead.longValue(), y);
        
        int magicRead = var.getMagic();
        assertEquals(magicRead, 0xFFFF);
    }

    
    
    public void test1() throws IOException
    {
        writeVarBitField(8, 9, 10);
    }

    public void test2() throws IOException
    {
        writeVarBitField(4, 9, 10);
    }

    public void test3() throws IOException
    {
        writeVarBitField(5, 9, 10);
    }

    public void test4() throws IOException
    {
        writeVarBitField(16, 100, 220);
    }

    public void test5() throws IOException
    {
        writeVarBitField(12, 9, 10);
    }

    public void test6() throws IOException
    {
        writeVarBitField(12, 900, 2000);
    }

    public void test7() throws IOException
    {
        writeVarBitField(3, 6, 7);
    }
}
