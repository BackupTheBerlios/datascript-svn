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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import datascript.runtime.CallChain;
import datascript.runtime.Mapping;
import datascript.runtime.io.BitStreamReader;
import datascript.runtime.io.BitStreamWriter;



public class UnsignedShortArray implements Array<Integer>, SizeOf
{
    int[] data; // data is between [offset... offset+length-1]
    int offset;
    int length;


    public UnsignedShortArray(BitStreamReader in, int length)
            throws IOException
    {
        if (length == -1)
        {
            // throw new RuntimeException("variable length " + getClass() + "
            // not implemented");

            List<Integer> v = new ArrayList<Integer>();
            long __afpos = 0;
            try
            {
                while (true)
                {
                    __afpos = in.getBitPosition();
                    v.add(in.readUnsignedShort());
                }
            }
            catch (IOException __e)
            {
                in.setBitPosition(__afpos);
            }
            data = new int[v.size()];
            for (int i = 0; i < data.length; ++i)
            {
                data[i] = v.get(i).intValue();
            }
        }
        else
        {
            this.length = length;
            this.data = new int[length];
            for (int i = 0; i < length; i++)
            {
                this.data[i] = in.readUnsignedShort();
            }
            this.offset = 0;
        }
    }


    public UnsignedShortArray(int length)
    {
        this(new int[length], 0, length);
    }


    public UnsignedShortArray(int[] data, int offset, int length)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof UnsignedShortArray)
        {
            UnsignedShortArray that = (UnsignedShortArray) obj;
            return (this.offset == offset) && (this.length == length)
                    && java.util.Arrays.equals(this.data, that.data);
        }
        return super.equals(obj);
    }


    public boolean equalsWithException(Object obj)
    {
        if (obj instanceof UnsignedShortArray)
        {
            UnsignedShortArray that = (UnsignedShortArray) obj;
            // not necessary to loop the array two times
//	        if (that.sizeof() != this.sizeof())
//	            throw new RuntimeException("size of arrays are different.");
	        if (that.data.length != this.data.length)
	            throw new RuntimeException("count of elements in arrays are different.");
	
	        for (int i = 0; i < this.data.length; i++)
	        {
	            if (this.data[i] != that.data[i])
	                throw new RuntimeException("index " + i + " do not match.");
	        }
	        return true;
        }
        return super.equals(obj);
    }


    public int elementAt(int i)
    {
        return data[offset + i];
    }


    public void setElementAt(int value, int i)
    {
        data[offset + i] = value;
    }


    public int length()
    {
        return length;
    }


    public int sizeof()
    {
        return 2 * length;
    }


    public int bitsizeof()
    {
        return 2 * 8 * length;
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
        for (int i : data)
        {
            retVal += i;
        }
        if (retVal > Integer.MAX_VALUE)
            throw new Exception("result is too big for an integer");
        return (int) retVal;
    }


    public Array<Integer> map(Mapping<Integer> m)
    {
        UnsignedShortArray result = new UnsignedShortArray(length);
        for (int i = 0; i < length; i++)
        {
            result.data[i] = m.map(new Integer(data[offset + i])).intValue();
        }
        return result;
    }


    public Array<Integer> subRange(int begin, int length)
    {
        if (begin < 0 || begin >= this.length || begin + length > this.length)
            throw new ArrayIndexOutOfBoundsException();
        return new UnsignedShortArray(data, offset + begin, length);
    }


    public void write(BitStreamWriter out, CallChain cc) throws IOException
    {
        for (int i = offset; i < offset + length; i++)
        {
            out.writeShort(data[i]);
        }
    }
    
    @Override
    public Iterator<Integer> iterator()
    {
        return new IntArrayIterator();
    }
    
    class IntArrayIterator implements Iterator<Integer>
    {
        private int index;

        @Override
        public boolean hasNext()
        {
            return index < data.length;
        }

        @Override
        public Integer next()
        {
            return data[index++];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        
    }
    
}
