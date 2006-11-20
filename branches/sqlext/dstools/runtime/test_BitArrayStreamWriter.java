package datascript.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class test_BitArrayStreamWriter {

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
		
		        ByteArrayOutputStream bs = new ByteArrayOutputStream();
		        BitArrayStreamWriter  i = new BitArrayStreamWriter(bs);
		     
		         
		        // Test ByteOrder.BIG_ENDIAN, the default.
		        
		        i.writeBits(57,7);  // 0111001    <- first bit 0 will be skiped when it is displayed.
		        i.writeUnsignedByte((short)56); // 00111000
		        i.writeBigInteger(new BigInteger("65535"),16); //1111111111111111
		        i.writeUnsignedByte((short)255); // 11111111
		        
//		        i.flush();
		        i.flush_all();
		        
		        byte[] result_byte_array = bs.toByteArray();
		        System.out.println("number of bytes = " + result_byte_array.length);
		        System.out.println(new BigInteger(result_byte_array).toString(2));
		        // result shoud be 11100100111000000000001111111111111111  +  0 <- addtional dummy by for flush_all()
		        System.out.println("end");
		       

		      }
		    catch(IOException e)
		      {
		        throw new RuntimeException(e);
		      }
		  }
		


		
		
	}


