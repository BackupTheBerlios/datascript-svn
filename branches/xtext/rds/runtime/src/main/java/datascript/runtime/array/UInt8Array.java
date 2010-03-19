/* BSD License
 *
 * Copyright (c) 2006-2009, Harald Wellmann, Harman/Becker Automotive Systems
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
import datascript.runtime.io.BitStreamReader;
import datascript.runtime.io.BitStreamWriter;

/**
 * @author hwe
 *
 */
public class UInt8Array extends AbstractArray<Short>
{
    private static final long serialVersionUID = -8583291153535920741L;

    @Override
    public int bitsizeof()
    {
        return 8*size();
    }

    @Override
    public int sizeof()
    {
        return size();
    }
    
    @Override
    public void read(BitStreamReader in, CallChain cc) throws IOException
    {
        int b;
        while ((b = in.read()) != -1)
        {
            add((short)b);
        }
    }

    @Override
    public void read(BitStreamReader in, CallChain cc, int numElems) throws IOException
    {
        for (int i = 0; i < numElems; i++)
        {
            short b = (short) in.readUnsignedByte();
            add(b);
        }
    }

    @Override
    public void write(BitStreamWriter out, CallChain cc) throws IOException
    {
        for (Short s : this)
        {
            out.writeByte(s);
        }
    }    
}
