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
import java.math.BigInteger;

import datascript.runtime.CallChain;
import datascript.runtime.Mapping;
import datascript.runtime.io.BitStreamReader;
import datascript.runtime.io.BitStreamWriter;

public class BitFieldArray implements Array, SizeOf
{
    BigInteger[] data; // data is between [offset... offset+length-1]

    int offset;

    /** Number of array elements. */
    int length;

    /** Number of bits per element. */
    int numBits;

    public BitFieldArray(BitStreamReader in, int length, int numBits)
            throws IOException
    {
        if (length == -1)
        {
            throw new RuntimeException("variable length " + getClass()
                    + " not implemented");
        }
        else
        {
            this.length = length;
            this.numBits = numBits;
            data = new BigInteger[length];
            for (int i = 0; i < length; i++)
            {
                data[i] = in.readBigInteger(numBits);
            }
            this.offset = 0;
        }
    }

    public BitFieldArray(int length, int numBits)
    {
        this(new BigInteger[length], 0, length, numBits);
    }

    public BitFieldArray(BigInteger[] data, int offset, int length, int numBits)
    {
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.numBits = numBits;
    }


    public boolean equals(Object obj)
    {
        if (obj instanceof BitFieldArray)
        {
            BitFieldArray that = (BitFieldArray) obj;
            return 
                (this.offset == offset) && 
                (this.length == length) && 
                (this.numBits == numBits) && 
                java.util.Arrays.equals(this.data, that.data);
        }
        return super.equals(obj);
    }


    public BigInteger elementAt(int i)
    {
        return data[offset + i];
    }

    public int length()
    {
        return length;
    }

    public int sizeof()
    {
        if ((numBits * length) % 8 != 0)
            return ((numBits * length) / 8) + 1;
        
        return (numBits * length) / 8;
    }

    public Array map(Mapping m)
    {
        BitFieldArray result = new BitFieldArray(length, numBits);
        for (int i = 0; i < length; i++)
        {
            result.data[i] = ((BigInteger) m.map(data[offset + i]));
        }
        return result;
    }

    public Array subRange(int begin, int length)
    {
        if (begin < 0 || begin >= this.length || begin + length > this.length)
            throw new ArrayIndexOutOfBoundsException();
        return new BitFieldArray(data, offset + begin, length, numBits);
    }

    public void write(BitStreamWriter out, CallChain cc) throws IOException
    {
        for (int i = offset; i < offset + length; i++)
        {
            out.writeBigInteger(data[i], numBits);
        }
    }
}
