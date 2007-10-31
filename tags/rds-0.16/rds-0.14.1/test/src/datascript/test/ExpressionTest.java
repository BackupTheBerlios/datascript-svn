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

/**
 * @author HWellmann
 *
 */
public class ExpressionTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "EnumerationTest.data";
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
    
    private void checkSequence(CondExpr ce, int size, int tag, int value)
    {
        assertEquals(tag, ce.getTag());
        CondBlock cb = ce.getBlock();
        assertEquals(value, cb.getValue());
        assertEquals(size, ce.sizeof());
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeSequence(int tag, int value) throws IOException
    {
        os = new FileImageOutputStream(file);
        os.writeByte(tag);
        os.writeInt(value);
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }
    
    public void testCondExpr1() throws Exception
    {
        int tag = 37;
        int size = writeSequence(tag, (tag % 2 == 0) ? 47 : 11);

        CondExpr ce = new CondExpr(fileName);
        checkSequence(ce, size, tag, (tag % 2 == 0) ? 47 : 11);
        
        ce.write(wFileName);

        CondExpr ce2 = new CondExpr(wFileName);
        checkSequence(ce2, size, tag, (tag % 2 == 0) ? 47 : 11);
    }
}
