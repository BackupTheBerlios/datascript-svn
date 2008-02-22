/**
 * 
 */
package datascript.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import junit.framework.TestCase;
import bits.ItemA;
import bits.ItemB;
import bits.arrays.CompoundArray;
import datascript.runtime.array.ObjectArray;
import datascript.runtime.io.DataScriptIO;

/**
 * @author HWellmann
 *
 */
public class CompoundArrayTest extends TestCase
{
    private FileImageOutputStream os;
    private String wFileName = "CompoundArryWriteTest.data";
    private String fileName = "arrayparsertest.bin";
    private File file = new File(fileName);

    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public CompoundArrayTest(String name)
    {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        file.delete();
    }
    
    private void checkArray(CompoundArray array, int size, 
    		int numItems, int valueA, long valueB)
    {
        ObjectArray<ItemA> a = array.getA();
        assertEquals(numItems, a.length());
        for (int i = 0; i < numItems ; i++)
        {
            ItemA itemA = a.elementAt(i);
            assertEquals(valueA+i, itemA.getValue());
        }
        int i = 0;
        for (ItemA itemA : a)
        {
            assertEquals(valueA+i, itemA.getValue());
            i++;
        }
        
        
        ObjectArray<ItemB> b = array.getB();
        assertEquals(numItems, b.length());
        for (i = 0; i < numItems; i++)
        {
            ItemB itemB = b.elementAt(i);
            assertEquals(valueB+i, itemB.getValue());
        }
        
        i = 0;
        for (ItemB itemB : b)
        {
            assertEquals(valueB+i, itemB.getValue());
            i++;
        }
        assertEquals(size, array.sizeof());
    }

    /*
     * Test method for 'datascript.library.BitStreamReader.readByte()'
     * return	size of the file
     */
    private int writeArray(int numItems, int valueA, long valueB) throws IOException
    {
        file.delete();
        os = new FileImageOutputStream(file);
        os.writeShort(numItems);

        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(1);
            os.writeShort(valueA+i);
        }
        for (int i = 0; i < numItems; i++)
        {
            os.writeByte(2);
            os.writeInt((int)valueB+i);
        }
        int size = (int) os.getStreamPosition();
        os.close();
        
        return size;
    }

    public void testArray1() throws Exception
    {
    	int size = writeArray(5, 20, 100000);

        CompoundArray array = new CompoundArray(fileName);
        checkArray(array, size, 5, 20, 100000);

        array.write(wFileName);

        CompoundArray array2 = new CompoundArray(wFileName);
        checkArray(array2, size, 5, 20, 100000);
        assertTrue(array.equals(array2));
    }

    public void testArray2() throws Exception
    {
        int size = writeArray(5000, 29000, 100000);

        CompoundArray array = new CompoundArray(fileName);
        checkArray(array, size, 5000, 29000, 100000);

        array.write(wFileName);

        CompoundArray array2 = new CompoundArray(wFileName);
        checkArray(array2, size, 5000, 29000, 100000);
        assertTrue(array.equals(array2));
    }
    
    public void testArrayFromList()
    {
        CompoundArray ca = new CompoundArray();
        short numItems = 3;
        ca.setNumItems(numItems);
        List<ItemA> a = new ArrayList<ItemA>(numItems);
        List<ItemB> b = new ArrayList<ItemB>(numItems);
        for (int i = 0; i < numItems; i++)
        {
            ItemA itemA = new ItemA((short)1, 11+i);
            a.add(itemA);
            ItemB itemB = new ItemB ((short)2, 22+i);
            b.add(itemB);
        }
        ca.setA(a);
        ca.setB(b);
        
        byte[] blob = DataScriptIO.write(ca);
        CompoundArray ca2 = DataScriptIO.read(CompoundArray.class, blob);
        assertEquals(ca, ca2);
    }
}
