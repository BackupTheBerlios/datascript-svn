package datascript.runtime;

import java.io.IOException;
import java.util.BitSet;

public class BitsRandomAccessWriter {
	
	BitSet bits;
	long pos;
	
	BitsRandomAccessWriter(){
		pos = 0;
		bits = new BitSet();
	}
	
	void seek(long pos){
		this.pos = pos;
	}
	
	void writeBits(long value, int size){
		
		for(int i = 0 ; i < size ; i++){
			pos += i;
			if( (value & 1) == 1 ) bits.set((int)pos);
			else bits.set((int)pos,0);
			value = value >> 1;
		}
		
	}
	
	void writeByte(byte value){
		
		for(int i = 0 ; i < 8 ; i++){
			pos += i;
			if( (value & 1) == 1 ) bits.set((int)pos);
			else bits.set((int)pos,0);
			value = (byte)(value >> 1);
		}
	}
	
	void writeShort(short value){
		for(int i = 0 ; i < 16 ; i++){
			pos += i;
			if( (value & 1) == 1 ) bits.set((int)pos);
			else bits.set((int)pos,0);
			value = (short)(value >> 1);
		}
	}
	
	void writeInt(int value){
		
		for(int i = 0 ; i < 32 ; i++){
			pos += i;
			if( (value & 1) == 1 ) bits.set((int)pos);
			else bits.set((int)pos,0);
			value = value >> 1;
		}
	}
	
	void writeLong(long value){
		
		for(int i = 0 ; i < 64 ; i++){
			pos += i;
			if( (value & 1) == 1 ) bits.set((int)pos);
			else bits.set((int)pos,0);
			value = value >> 1;
		}
	}
	
	void Fully(byte[] b_array){
		long j = 0;
		
		for(int i = (int)pos ; i < bits.length() ; i++){
			if( bits.get(i) == true ) b_array[(int)j] = 1;
			else b_array[(int)j] = 0;
			++j;
		}
		pos = bits.length();
		
	}
	
	public long getBitPosition() throws IOException
	{
		return pos;
	}
	
	public void setBitPosition(long pos) throws IOException
	{
		seek((int)pos);
	}
	
}
