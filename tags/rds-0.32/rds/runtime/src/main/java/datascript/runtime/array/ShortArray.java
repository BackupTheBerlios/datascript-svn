/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package datascript.runtime.array;


import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;

import datascript.runtime.CallChain;
import datascript.runtime.DataScriptError;
import datascript.runtime.Mapping;
import datascript.runtime.io.BitStreamWriter;



public class ShortArray implements Array<Short>, SizeOf
{
    short[] data; // data is between [offset... offset+length-1]
    int offset;
    int length;


    public ShortArray(DataInput in, int length) throws IOException
    {
        if (length == -1)
        {
            throw new UnsupportedOperationException("variable length " + getClass()
                    + " not implemented");
        }
        else
        {
            this.length = length;
            data = new short[length];
            for (int i = 0; i < length; i++)
            {
                data[i] = in.readShort();
            }
            this.offset = 0;
        }
    }


    public ShortArray(int length)
    {
        this(new short[length], 0, length);
    }


    public ShortArray(short[] data, int offset, int length)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ShortArray)
        {
            ShortArray that = (ShortArray) obj;
	        if (that.length != this.length)
	        	return false;

	        for (int i = 0; i < this.length; i++)
	        {
	            if (this.elementAt(i) != that.elementAt(i))
	            	return false;
	        }
	        return true;
        }
        return super.equals(obj);
    }


    public boolean equalsWithException(Object obj)
    {
        if (obj instanceof ShortArray)
        {
            ShortArray that = (ShortArray) obj;
	        if (that.length != this.length)
	            throw new DataScriptError("mismatched array length");
	
	        for (int i = 0; i < this.length; i++)
	        {
	            if (this.elementAt(i) != that.elementAt(i))
	                throw new DataScriptError("value mismatch at index " + i);
	        }
	        return true;
        }
        return super.equals(obj);
    }


    public short elementAt(int i)
    {
        return data[offset + i];
    }


    public void setElementAt(short value, int i)
    {
        data[offset + i] = value;
    }


    public int length()
    {
        return length;
    }


    /**
     * This function sums up all values of an array and returns the value
     * 
     * @return	sum of all array values
     * @throws Exception
     */
    public int sum() throws Exception
    {
        long retVal = 0;
        for (short si : data)
        {
            retVal += si;
        }
        if (retVal > Integer.MAX_VALUE)
            throw new DataScriptError("result is too big for an integer");
        return (int) retVal;
    }


    public int sizeof()
    {
        return 2 * length;
    }


    public int bitsizeof()
    {
        return 2 * 8 * length;
    }


    public Array<Short> map(Mapping<Short> m)
    {
        ShortArray result = new ShortArray(length);
        for (int i = 0; i < length; i++)
        {
            result.data[i] = m.map(new Short(data[offset + i])).shortValue();
        }
        return result;
    }


    public Array<Short> subRange(int begin, int len)
    {
        if (begin < 0 || begin >= this.length || begin + len > this.length)
            throw new ArrayIndexOutOfBoundsException();
        return new ShortArray(data, offset + begin, len);
    }


    public void write(BitStreamWriter out, CallChain cc) throws IOException
    {
        for (int i = offset; i < offset + length; i++)
        {
            out.writeShort(data[i]);
        }
    }
    
    @Override
    public Iterator<Short> iterator()
    {
        return new ShortArrayIterator();
    }
    
    class ShortArrayIterator implements Iterator<Short>
    {
        private int index;

        @Override
        public boolean hasNext()
        {
            return index < length;
        }

        @Override
        public Short next()
        {
            return data[offset+index++];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
