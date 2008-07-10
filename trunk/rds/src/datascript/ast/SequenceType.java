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


package datascript.ast;


@SuppressWarnings("serial")
public class SequenceType extends CompoundType
{

    public SequenceType()
    {
    }


    @Override
    public IntegerValue sizeof(Scope ctxt)
    {
        IntegerValue eight = new IntegerValue(8);
        IntegerValue size = bitsizeof(ctxt);
        if (size.remainder(eight).compareTo(new IntegerValue(0)) != 0) 
        {
            throw new RuntimeException("sizeof not integer: " + size);
        }
        return size.divide(eight);
    }


    public IntegerValue bitsizeof(Scope ctxt)
    {
        IntegerValue size = new IntegerValue(0);

        for (int i = 0; i < fields.size(); i++)
        {
            Field fi = fields.get(i);
            /*
             * TODO: 
             * try
             * {
             *     StdIntegerType b = StdIntegerType.getBuiltinType(fi.getFieldType());
             *     if (b instanceof BitFieldType)
             *     {
             *         size = size.add(new IntegerValue(((BitFieldType) b).getLength()));
             *         continue;
             *     }
             * }
             * catch (ClassCastException _)
             * {
             * }
             */
            size = size.add(fi.bitsizeof(ctxt));
        }
        return size;
    }


    @Override
    public boolean isMember(Scope ctxt, Value val)
    {
        // do something like
        // if val.getType() == this
        throw new ComputeError("isMember() not implemented in "
                + this.getClass().getName());
    }


    @Override
    public String toString()
    {
        return "SEQUENCE";
    }
}
