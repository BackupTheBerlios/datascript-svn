/**
 * 
 */
package datascript.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;
import datascript.runtime.io.DataScriptIO;
import func.ArrayFunc;
import func.Bool;
import func.Inner;
import func.Inner3;
import func.Item;
import func.ItemRef;
import func.ItemRef3;
import func.OuterArray;
import func.OuterArray3;
import func.VarInt;

/**
 * @author HWellmann
 *
 */
public class FunctionTest extends TestCase
{
    private VarInt encode(int aValue)
    {
        VarInt result = new VarInt();
        if (aValue < 254)
        {
            result.setVal1((short)aValue);
        }
        else if (aValue <= 0xFFFF)
        {
            result.setVal1((short)255);
            result.setVal2(aValue);
        }
        else
        {
            result.setVal1((short)254);
            result.setVal3(new Long(aValue));
        }
        return result;
    }
    
    private byte[] writeToByteArray(int aValue) throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        if (aValue < 254)
        {
            writer.writeByte(aValue);
        }
        else if (aValue <= 0xFFFF)
        {
            writer.writeByte(255);
            writer.writeShort(aValue);
        }
        else
        {
            writer.writeByte(254);
            writer.writeUnsignedInt(aValue);
        }
        writer.close();
        return writer.toByteArray();
    }
    
    private void encodeAndDecodeValue(int input) throws Exception
    {
        VarInt vi = encode(input);
        int output = (int) vi.value();
        assertEquals(output, input);
        
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        vi.write(writer);
        writer.close();
        
        
        byte[] blob = writeToByteArray(input);
        byte[] decoded = writer.toByteArray();
        assertTrue(Arrays.equals(decoded, blob));
        
        ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blob);
        VarInt vi2 = new VarInt(reader);
        
        assertEquals(vi2, vi);        
    }
    
    public void testEncodeDecode42()  throws Exception
    {
        encodeAndDecodeValue(42);
    }

    public void testEncodeDecode253()  throws Exception
    {
        encodeAndDecodeValue(253);
    }

    public void testEncodeDecode254()  throws Exception
    {
        encodeAndDecodeValue(254);
    }
    
    public void testEncodeDecode255()  throws Exception
    {
        encodeAndDecodeValue(255);
    }
    
    public void testEncodeDecode1000() throws Exception
    {
        encodeAndDecodeValue(1000);
    }
    
    public void testEncodeDecode87654() throws Exception
    {
        encodeAndDecodeValue(87654);
    }
    
    public void testEncodeDecodeArrayFunc() throws Exception
    {
        ArrayFunc arrayFunc = new ArrayFunc();
        arrayFunc.setNumElems(3);
        List<Item> items = new ArrayList<Item>();

        Item item0 = new Item();
        item0.setA((short) 12);
        item0.setB((short) 13);
        items.add(item0);

        Item item1 = new Item();
        item1.setA((short) 19);
        item1.setB((short) 18);
        items.add(item1);

        Item item2 = new Item();
        item2.setA((short) 17);
        item2.setB((short) 14);
        items.add(item2);
        
        arrayFunc.setValues(items);
        arrayFunc.setPos(1);
        
        assertEquals(item1, arrayFunc.elem());
        
        byte[] bytes = DataScriptIO.write(arrayFunc);        
        ArrayFunc arrayFunc2 = DataScriptIO.read(ArrayFunc.class, bytes);
        
        assertEquals(item1, arrayFunc2.elem());
        assertEquals(arrayFunc, arrayFunc2);
    }


    public void testEncodeDecodeOuterArray() throws Exception
    {
        OuterArray outer = new OuterArray();
        outer.setNumElems(3);
        List<Item> items = new ArrayList<Item>();

        Item item0 = new Item();
        item0.setA((short) 12);
        item0.setB((short) 13);
        items.add(item0);

        Item item1 = new Item();
        item1.setA((short) 20);
        item1.setB((short) 18);
        items.add(item1);

        Item item2 = new Item();
        item2.setA((short) 17);
        item2.setB((short) 14);
        items.add(item2);
        
        outer.setValues(items);
        
        Inner inner = new Inner();
        inner.setIsExplicit((short)0);
        ItemRef ref = new ItemRef();
        ref.setPos((short)1);
        inner.setRef(ref);
        inner.setExtra(4711);
        
        outer.setInner(inner);
        
        byte[] bytes = DataScriptIO.write(outer);        
        OuterArray outer2 = DataScriptIO.read(OuterArray.class, bytes);
        
        assertEquals(outer, outer2);
    }

    public void testEncodeDecodeOuterArray3() throws Exception
    {
        OuterArray3 outer = new OuterArray3();
        outer.setNumElems(3);
        List<Item> items = new ArrayList<Item>();

        Item item0 = new Item();
        item0.setA((short) 12);
        item0.setB((short) 13);
        items.add(item0);

        Item item1 = new Item();
        item1.setA((short) 20);
        item1.setB((short) 18);
        items.add(item1);

        Item item2 = new Item();
        item2.setA((short) 17);
        item2.setB((short) 14);
        items.add(item2);
        
        outer.setValues(items);
        
        Inner3 inner = new Inner3();
        inner.setIsExplicit(Bool.FALSE);
        ItemRef3 ref = new ItemRef3();
        ref.setPos((short)1);
        inner.setRef(ref);
        inner.setExtra(4711);
        
        outer.setInner(inner);
        
        byte[] bytes = DataScriptIO.write(outer);        
        OuterArray3 outer2 = DataScriptIO.read(OuterArray3.class, bytes);
        
        assertEquals(outer, outer2);
    }

}
