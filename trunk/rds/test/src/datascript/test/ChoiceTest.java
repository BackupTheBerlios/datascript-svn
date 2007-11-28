/**
 * 
 */


package datascript.test;


import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import choices.Container;
import choices.Content;
import choices.ParamContainer;
import choices.ParamContent;
import choices.Selector;
import datascript.runtime.io.DataScriptIO;



/**
 * @author HWedekind
 * 
 */
public class ChoiceTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "CoicesTest.data";
    private String fileName = "choicestest.bin";
    private File file = new File(fileName);


    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        file.delete();
    }


    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeContainer(int tag, int value) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(tag);

        switch (tag)
        {
            case 1:
                os.writeByte(value);
                break;
            case 2:
            case 3:
            case 4:
                os.writeShort(value);
                break;
            default:
                os.writeInt(value);
        }
        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }


    private void checkContainer(Container c, int size, int tag, int value)
            throws IOException
    {
        assertEquals(tag, c.getTag());
        Content u = c.getContent();
        switch (tag)
        {
            case 1:
                assertEquals(value, u.getA());
                break;
            case 2:
            case 3:
            case 4:
                assertEquals(value, u.getB());
                break;
            default:
                assertEquals(value, u.getC());
        }
        assertEquals(size, c.sizeof());
    }


    public void testChoices1() throws Exception
    {
        int size = writeContainer(1, 0x5a);
        Container b = new Container(fileName);
        checkContainer(b, size, 1, 0x5a);

        b.write(wFileName);

        Container b2 = new Container(wFileName);
        checkContainer(b2, size, 1, 0x5a);
        assertTrue(b.equals(b2));
    }


    public void testChoices2() throws Exception
    {
        int size = writeContainer(3, 0x5a5a);
        Container b = new Container(fileName);
        checkContainer(b, size, 3, 0x5a5a);

        b.write(wFileName);

        Container b2 = new Container(wFileName);
        checkContainer(b2, size, 3, 0x5a5a);
        assertTrue(b.equals(b2));
    }


    public void testChoices3() throws Exception
    {
        int size = writeContainer(99, 0x5a5a5a5a);
        Container b = new Container(fileName);
        checkContainer(b, size, 99, 0x5a5a5a5a);

        b.write(wFileName);

        Container b2 = new Container(wFileName);
        checkContainer(b2, size, 99, 0x5a5a5a5a);
        assertTrue(b.equals(b2));
    }


    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     */
    private int writeParamContainer(int sel, int value) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeByte(sel);

        if (sel == Selector.BLACK.getValue())
            os.writeShort(value);
        else if (sel == Selector.WHITE.getValue())
            os.writeInt(value);
        else
            assertNotNull("selector not in range", null);
        int size = (int) os.getStreamPosition();
        os.close();

        return size;
    }


    private void checkParamContainer(ParamContainer c, int size, int tag, int value)
            throws IOException
    {
        assertEquals(tag, c.getSelector().getValue());
        ParamContent u = c.getContent();
        if (tag == Selector.BLACK.getValue())
            assertEquals(value, u.getBlack());
        else if (tag == Selector.WHITE.getValue())
            assertEquals(value, u.getWhite());
        else
            assertNotNull("selector not in range", null);
        assertEquals(size, c.sizeof());
    }


    public void testParamChoices1() throws Exception
    {
        int size = writeParamContainer(Selector.BLACK.getValue(), 0x5a5a);
        ParamContainer b = new ParamContainer(fileName);
        checkParamContainer(b, size, Selector.BLACK.getValue(), 0x5a5a);

        b.write(wFileName);

        ParamContainer b2 = new ParamContainer(wFileName);
        checkParamContainer(b2, size, Selector.BLACK.getValue(), 0x5a5a);
        assertTrue(b.equals(b2));
    }


    public void testParamChoices2() throws Exception
    {
        int size = writeParamContainer(Selector.WHITE.getValue(), 0x5a5a5a5a);
        ParamContainer b = new ParamContainer(fileName);
        checkParamContainer(b, size, Selector.WHITE.getValue(), 0x5a5a5a5a);

        b.write(wFileName);

        ParamContainer b2 = new ParamContainer(wFileName);
        checkParamContainer(b2, size, Selector.WHITE.getValue(), 0x5a5a5a5a);
        assertTrue(b.equals(b2));
    }
    
    public void testParamChoices3()
    {
        ParamContainer container = new ParamContainer();
        container.setSelector(Selector.GREY);
        container.setContent(new ParamContent());
        
        byte[] blob = DataScriptIO.write(container);
        
        assertEquals(1, blob.length);
    }
}
