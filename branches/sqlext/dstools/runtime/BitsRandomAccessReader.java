package datascript.runtime;

import java.io.IOException;
import java.util.BitSet;

public class BitsRandomAccessReader {
	
	BitSet bits;
	long pos;
	
	void seek(long pos){
		this.pos = pos;
	}
	
	long readBits(long size){
		
		
		long result = 0;
		
		for(int i = 0 ; i < size ; i++){
			pos += i;
			if( bits.get((int)pos) == true ){
				result += 1 << (size - i - 1);
			}
		}
		return result;
	}
	
	byte readByte(){
		byte result = 0;
		
		for(int i = 0 ; i < 8 ; i++){
			pos += i;
			if( bits.get((int)pos) == true ){
				result += 1 << (8 - i - 1);
			}
		}
		return result;
	}
	
	short readShort(){
		short result = 0;
		
		for(int i = 0 ; i < 16 ; i++){
			pos += i;
			if( bits.get((int)pos) == true ){
				result += 1 << (16 - i - 1);
			}
		}
		return result;
	}
	
	int readInt(){
		int result = 0;
		
		for(int i = 0 ; i < 32 ; i++){
			pos += i;
			if( bits.get((int)pos) == true ){
				result += 1 << (32 - i - 1);
			}
		}
		return result;
	}
	
	long readLong(){
		long result = 0;
		
		for(int i = 0 ; i < 64 ; i++){
			pos += i;
			if( bits.get((int)pos) == true ){
				result += 1 << (64 - i - 1);
			}
		}
		return result;
	}
	
	void readFully(byte[] b_array){
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
