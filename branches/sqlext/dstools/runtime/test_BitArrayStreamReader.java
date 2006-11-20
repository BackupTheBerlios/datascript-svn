package datascript.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class test_BitArrayStreamReader {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		try
		      {
		        // Test unsigned values.
		        byte[] b = new byte[]
		          { 
		            (byte) 0x72,
		            (byte) 0x70,
		            (byte) 0x05,
		            (byte) 0x77,
		            (byte) 0xac,
		            (byte) 0xf2,
		            (byte) 0x3b,
		            (byte) 0x67
		          };
		       
		        BitArrayStreamReader  i = new BitArrayStreamReader(b);
		         
		        // Test ByteOrder.BIG_ENDIAN, the default.
		        System.out.println(i.read() == 114);
		        i.seek(0);
		        System.out.println(i.readBoolean() == true);
		        i.seek(0);
		        System.out.println(i.readByte() == 114);
		        i.seek(0);
		        System.out.println(i.readChar() == '\u7270');
		        i.seek(0);
		        System.out.println(Double.compare(i.readDouble(), 1.709290273164385E243) == 0);
		        i.seek(0);
		        System.out.println(Float.compare(i.readFloat(), 4.7541126E30f) == 0);
		        i.seek(0);
		        System.out.println(i.readInt() == 1919944055);
		        i.seek(0);
		        System.out.println(i.readLong() == 8246096929276181351L);
		        i.seek(0);
		        System.out.println(i.readShort() == 29296);
		        i.seek(0);
		        System.out.println(i.readUnsignedByte() == 114);
		        i.seek(0);
		        System.out.println(i.readUnsignedInt() == 1919944055);
		        i.seek(0);
		        System.out.println(i.readUnsignedShort() == 29296);
		
		        // Test ByteOrder.LITTLE_ENDIAN.
		        i.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		        i.seek(0);
		        System.out.println(i.read() == 114);
		        i.seek(0);
		        System.out.println(i.readBoolean() == true);
		        i.seek(0);
		        System.out.println(i.readByte() == 114);
		        i.seek(0);
		        System.out.println(i.readChar() == '\u7072');
		        i.seek(0);
		        System.out.println(Double.compare(i.readDouble(), 1.9456609400629563E189) == 0);
		        i.seek(0);
		        System.out.println(Float.compare(i.readFloat(), 2.7064693E33f) == 0);
		        i.seek(0);
		        System.out.println(i.readInt() == 1996845170);
		        i.seek(0);
		        System.out.println(i.readLong() == 7438806032077647986L);
		        i.seek(0);
		        System.out.println(i.readShort() == 28786);
		        i.seek(0);
		        System.out.println(i.readUnsignedByte() == 114);
		        i.seek(0);
		        System.out.println(i.readUnsignedInt() == 1996845170);
		        i.seek(0);
		        System.out.println(i.readUnsignedShort() == 28786);
		        i.seek(0);
		        // 11100101110001( 0x7270 )
		        System.out.println(i.readBits(7) == 57);  // 111001
		        System.out.println(i.readUnsignedByte() == 56); // 011100
		
		        // Test unsigned values.
		        b = new byte[]
		          { 
		            (byte) 0x92,
		           (byte) 0x80,
		            (byte) 0x05,
		            (byte) 0x77,
		            (byte) 0xac,
		            (byte) 0xf2,
		            (byte) 0x8b,
		            (byte) 0xa7
		          };
		
		      
		        i = new BitArrayStreamReader(b);
		
		        // Test ByteOrder.BIG_ENDIAN, the default.
		        System.out.println(i.read() == 146);
		        i.seek(0);
		        System.out.println(i.readBoolean() == true);
		        i.seek(0);
		        System.out.println(i.readByte() == -110);
		        i.seek(0);
		        System.out.println(i.readChar() == '\u9280');
		        i.seek(0);
		        System.out.println(Double.compare(i.readDouble(), -1.4183142849706364E-219) == 0);
		        i.seek(0);
		        System.out.println(Float.compare(i.readFloat(), -8.079283E-28f) == 0);
		        i.seek(0);
		        System.out.println(i.readInt() == -1837103753);
		        i.seek(0);
		        System.out.println(i.readLong() == -7890300535592285273L);
		        i.seek(0);
		        System.out.println(i.readShort() == -28032);
		        i.seek(0);
		        System.out.println(i.readUnsignedByte() == 146);
		        i.seek(0);
		        System.out.println(i.readUnsignedInt() == 2457863543L);
		        i.seek(0);
		        System.out.println(i.readUnsignedShort() == 37504);
		
		        // Test ByteOrder.LITTLE_ENDIAN.
		        i.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		        i.seek(0);
		        System.out.println(i.read() == 146);
		        i.seek(0);
		        System.out.println(i.readBoolean() == true);
		        i.seek(0);
		        System.out.println(i.readByte() == -110);
		        i.seek(0);
		        System.out.println(i.readChar() == '\u8092');
		        i.seek(0);
		        System.out.println(Double.compare(i.readDouble(), -3.463391436203922E-118) == 0);
		        i.seek(0);
		        System.out.println(Float.compare(i.readFloat(), 2.707747E33f) == 0);
		        i.seek(0);
		        System.out.println(i.readInt() == 1996849298);
		        i.seek(0);
		        System.out.println(i.readLong() == -6373734025067659118L);
		        i.seek(0);
		        System.out.println(i.readShort() == -32622);
		        i.seek(0);
		        System.out.println(i.readUnsignedByte() == 146);
		        i.seek(0);
		        System.out.println(i.readUnsignedInt() == 1996849298);
		        i.seek(0);
		        System.out.println(i.readUnsignedShort() == 32914);
		      }
		    catch(IOException e)
		      {
		        throw new RuntimeException(e);
		      }
		  }
		


		
		
	}


