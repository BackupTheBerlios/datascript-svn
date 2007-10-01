/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
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

import datascript.runtime.CallChain;
import datascript.runtime.Mapping;
import datascript.runtime.io.BitStreamReader;
import datascript.runtime.io.BitStreamWriter;



public class StringArray implements Array, SizeOf
{
    private String[] data; // data is between [offset... offset+length-1]
    int offset;
    int length;


    public StringArray(int length)
    {
        this(new String[length], 0, length);
    }


    public StringArray(String[] data, int offset, int length)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
    }


    public StringArray(BitStreamReader in, int length) throws IOException
    {
        if (length == -1)
        {
            throw new RuntimeException("variable length " + getClass()
                    + " not implemented");
        }
        else
        {
            this.length = length;
            data = new String[length];
            for (int i = 0; i < length; i++)
            {
                data[i] = in.readString();
            }
            this.offset = 0;
        }
    }


    public boolean equals(Object obj)
    {
        if (obj instanceof StringArray)
        {
            StringArray that = (StringArray) obj;
            return (this.offset == offset) && (this.length == length)
                    && java.util.Arrays.equals(this.data, that.data);
        }
        return super.equals(obj);
    }


    public boolean equalsWithException(StringArray that)
    {
        if (that.sizeof() != this.sizeof())
            throw new RuntimeException("size of arrays are different.");
        if (that.data.length != this.data.length)
            throw new RuntimeException("count of elements in arrays are different.");

        for (int i = 0; i < this.data.length; i++)
        {
            if (!this.data.equals(that.data[i]))
                throw new RuntimeException("index " + i + " do not match.");
        }
        return true;
    }


    public String elementAt(int i)
    {
        return data[offset + i];
    }


    public int length()
    {
        return length;
    }


    public Array map(Mapping m)
    {
        StringArray result = new StringArray(length);
        for (int i = 0; i < length; i++)
        {
            result.data[i] = (String) m.map(new String(elementAt(i)));
        }
        return result;
    }


    public Array subRange(int begin, int length)
    {
        if (begin < 0 || begin >= this.length || begin + length > this.length)
            throw new ArrayIndexOutOfBoundsException();
        return new StringArray(data, offset + begin, length);
    }


    public void write(BitStreamWriter out, CallChain cc) throws IOException
    {
        for (int i = offset; i < offset + length; i++)
        {
            out.writeString(data[i]);
        }
    }


    public int sizeof()
    {
        int size = 0;
        for (int i = offset; i < offset + length; i++)
            size += data[i].length();
        return (size + 1) << 3;
    }

}
