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
import java.util.ArrayList;
import java.util.ListIterator;

import datascript.runtime.CallChain;
import datascript.runtime.DataScriptError;
import datascript.runtime.Verifiable;
import datascript.runtime.Verifier;
import datascript.runtime.io.BitStreamReader;


/**
 * @author hwe
 *
 */
abstract public class AbstractArray<E> extends ArrayList<E> 
    implements Verifiable, SizeOf, Streamable
{
    private static final long serialVersionUID = 471550870498902529L;

    @Override
    public int sizeof()
    {
        int bitSize = bitsizeof();
        
        if (bitSize % 8 != 0)
            throw new DataScriptError("bit size is not divisible by 8");
        
        return bitSize/8;
    }
    
    @Override
    public void assertEqualTo(Object obj)
    {
        if (getClass() != obj.getClass())
        {
            throw new DataScriptError("class mismatch : " 
                + getClass().getName() + " != " + obj.getClass().getName() );
        }
        
        AbstractArray<?> other = (AbstractArray<?>) obj;
        int index = 0;
        ListIterator<E> it1 = listIterator();
        ListIterator<?> it2 = other.listIterator();
        
        while (it1.hasNext() && it2.hasNext()) 
        {
            E o1 = it1.next();
            Object o2 = it2.next();
            if (o1 == null)
            {
                if (o2 != null)
                {
                    throw new DataScriptError("element is null at index " + index);
                }
            }
            else
            {
                try
                {
                    Verifier.assertEquals(o1, o2);
                }
                catch (AssertionError exc)
                {
                    throw new DataScriptError("mismatch at index " + index, exc);
                }
            }
        }
        
        if (it1.hasNext() || it2.hasNext())
        {
            throw new DataScriptError("array sizes not equal");
        }
    }
    
    abstract public void read(BitStreamReader in, CallChain cc, int numElems) 
        throws IOException;
}
