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
package datascript.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * @author HWellmann
 *
 */
public abstract class BitStreamWriter extends MemoryCacheImageOutputStream
{
    protected OutputStream os;
    
    public BitStreamWriter(OutputStream os)
    {
        super(os);
        this.os = os;
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
    
    public void writeBigInteger(BigInteger value, int numBits) throws IOException
    {
/*
        BigInteger result = BigInteger.ZERO;
        int toBeRead = numBits;
        if (toBeRead > 8)
        {
            if (bitOffset != 0)
            {
                int prefixLength = 8-bitOffset;
                long mostSignificantBits = writeBits(prefixLength);
                result = BigInteger.valueOf(mostSignificantBits);
                toBeRead -= prefixLength;
            }

            int numBytes = toBeRead / 8;
            byte[] b = new byte[numBytes];
            writeFully(b);
            BigInteger i = new BigInteger(1, b);
            result = result.shiftLeft(8*numBytes);
            result = result.or(i);
            toBeRead %= 8;
        }
        if (toBeRead > 0)
        {
            long value = writeBits(toBeRead);
            result = result.shiftLeft(toBeRead);
            result = result.or(BigInteger.valueOf(value));
        }        
        return result;
*/
    }
}
