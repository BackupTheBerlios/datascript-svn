/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.CondBlock;
import bits.CondExpr;
import bits.ParamBlock;
import datascript.runtime.UnsignedShortArray;

/**
 * @author HWellmann
 *
 */
public class ExpressionTest extends TestCase
{
    private FileImageOutputStream os;
    private String fileName = "expressiontest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public ExpressionTest(String name)
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
    private void writeSequence(int tag) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(tag);
        int value = (tag % 2 == 0) ? 47 : 11;
        os.writeInt(value);
        int size = (int) os.getStreamPosition();
        os.close();

        CondExpr ce = new CondExpr(fileName);
        assertEquals(tag, ce.getTag());
        CondBlock cb = ce.getBlock();
        assertEquals(value, cb.getValue());
        assertEquals(size, ce.sizeof());
    }
    
    public void testCondExpr1() throws IOException
    {
        writeSequence(37);
    }
}
