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
package datascript.runtime.io;

import java.io.IOException;
import java.math.BigInteger;

import javax.imageio.stream.ImageInputStreamImpl;

/**
 * @author HWellmann
 *
 */
public abstract class BitStreamReader extends ImageInputStreamImpl
{
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
    
    public byte readByte() throws IOException
    {
        byte result;
        if (bitOffset == 0)
        {
            result = super.readByte();
        }
        else
        {
            result = (byte) readBits(8);            
        }
        return result;
    }

    public int readUnsignedByte() throws IOException
    {
        int result;
        if (bitOffset == 0)
        {
            result = super.readUnsignedByte();
        }
        else
        {
            result = (int) (readBits(8) & 0xFF);            
        }
        return result;
    }

    public short readShort() throws IOException
    {
        short result;
        if (bitOffset == 0)
        {
            result = super.readShort();
        }
        else
        {
            result = (short) readBits(16);            
        }
        return result;
    }

    public int readUnsignedShort() throws IOException
    {
        int result;
        if (bitOffset == 0)
        {
            result = super.readUnsignedShort();
        }
        else
        {
            result = (int) (readBits(16) & 0xFFFF);            
        }
        return result;
    }
    
    public int readInt() throws IOException
    {
        int result;
        if (bitOffset == 0)
        {
            result = super.readInt();
        }
        else
        {
            result = (int) readBits(32);            
        }
        return result;
    }

    public long readUnsignedInt() throws IOException
    {
        long result;
        if (bitOffset == 0)
        {
            result = super.readUnsignedInt();
        }
        else
        {
            result = readBits(32);            
        }
        return result;
    }

    public long readLong() throws IOException
    {
        long result;
        if (bitOffset == 0)
        {
            result = super.readLong();
        }
        else
        {
            result = readBits(64);            
        }
        return result;
    }
    
    public BigInteger readBigInteger(int numBits) throws IOException
    {
        BigInteger result = BigInteger.ZERO;
        int toBeRead = numBits;
        if (toBeRead > 8)
        {
            if (bitOffset != 0)
            {
                int prefixLength = 8-bitOffset;
                long mostSignificantBits = readBits(prefixLength);
                result = BigInteger.valueOf(mostSignificantBits);
                toBeRead -= prefixLength;
            }

            int numBytes = toBeRead / 8;
            byte[] b = new byte[numBytes];
            readFully(b);
            BigInteger i = new BigInteger(1, b);
            result = result.shiftLeft(8*numBytes);
            result = result.or(i);
            toBeRead %= 8;
        }
        if (toBeRead > 0)
        {
            long value = readBits(toBeRead);
            result = result.shiftLeft(toBeRead);
            result = result.or(BigInteger.valueOf(value));
        }        
        return result;        
    }
    
    public String readString() throws IOException
    {
        String result = "";
        while (true)
        {
            byte characterByte = this.readByte();
            if (characterByte == 0)
                break;
            result += (char)characterByte;
        }
        return result;
    }
}
