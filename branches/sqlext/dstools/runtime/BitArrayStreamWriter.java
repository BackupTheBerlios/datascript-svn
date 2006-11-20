
package datascript.runtime;


import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import javax.imageio.stream.MemoryCacheImageOutputStream;


public class BitArrayStreamWriter extends MemoryCacheImageOutputStream
{
	
    public BitArrayStreamWriter(OutputStream arg0) {
		super(arg0);
	}

	public long getBitPosition() throws IOException
    {
        long pos = 8*streamPos + bitOffset;
        return pos;
    }
    
    public void setBitPosition(long pos) throws IOException
    {
        int newBitOffset = (int) (pos % 8);
        long newBytePos  = pos / 8;
        seek(newBytePos);
        if (newBitOffset != 0)
        {
            setBitOffset(newBitOffset);
        }       
    }
    
    public void writeByte(int value) throws IOException
    {
        if (bitOffset == 0)
        {
            super.writeByte(value);
        }
        else
        {
            writeBits((long) value, 8);            
        }
    }

    public void writeUnsignedByte(short value) throws IOException
    {
    	writeBits(value,8);            
        
    }

    public void writeShort(int value) throws IOException
    {
        if (bitOffset == 0)
        {
        	 super.writeShort(value);
        }
        else
        {
        	writeBits((long)value, 16);            
        }
    }
    
    public void writeUnsignedShort(int value) throws IOException
    {
    	writeBits(value,16);            
        
    }

    
    public void writeInt(int value) throws IOException
    {
        if (bitOffset == 0)
        {
        	 super.writeInt(value);
        }
        else
        {
        	writeBits((long)value, 32);            
        }
    }

    public void writeUnsignedInt(long value) throws IOException
    {
    	writeBits(value, 32);            
    }

    public void writeLong(long value) throws IOException
    {
        if (bitOffset == 0)
        {
        	 super.writeLong(value);
        }
        else
        {
        	writeBits(value, 64);            
        }
    }
    
    public void byteAlign() throws IOException
    {
        if (bitOffset != 0)
        {
        	writeBits(0, 8-bitOffset);
        }
    }
    
    public void writeBigInteger(BigInteger value,int numBits) throws IOException
    {
    	byte[] byte_array = value.toByteArray();
    	if (bitOffset != 0)
    	{
    		for(int i = 0; i < byte_array.length  ;i++){
    			writeBits(byte_array[i],8);
    		}
    	}else {
    		write(byte_array);
    	}
    }
    
    public void flush_all() throws IOException{
//    	super.flush();
    	if(bitOffset !=0 ){
    		super.writeBits(0,8-bitOffset);
    	}
    	super.flush();
    	
    	
    }
    
    @Override
	public void write(int arg) throws IOException {
			super.write(arg);
	}

	@Override
	public void write(byte[] arg0, int arg1, int arg2) throws IOException {
		super.write(arg0,arg1,arg2);
	}
	
	@Override
	public int read() throws IOException {
		
		return super.read();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return super.read(arg0,arg1,arg2);
	}
   
}
