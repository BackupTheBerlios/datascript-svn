/**
 * 
 */
package datascript.test;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import datascript.runtime.io.ByteArrayBitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamWriter;
import func.VarInt;

/**
 * @author HWellmann
 *
 */
public class FunctionTest extends TestCase
{
    /**
     * Constructor for BitStreamReaderTest.
     * @param name
     */
    public FunctionTest(String name)
    {
        super(name);
    }

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
            result.setVal3(aValue);
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
}
