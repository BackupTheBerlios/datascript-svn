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
package datascript;

import java.util.Iterator;

public class StructType extends CompoundType
{

    StructType(String name, CompoundType parent)
    {
        super(name, parent);
    }

    public IntegerValue sizeof(Context ctxt)
    {
        IntegerValue size = new IntegerValue(0);
        IntegerValue eight = new IntegerValue(8);

        for (int i = 0; i < fields.size(); i++)
        {
            Field fi = (Field) fields.elementAt(i);
            try
            {
                BuiltinType b = BuiltinType.getBuiltinType(fi.getType());
                if (b instanceof BitFieldType)
                {
                    size = size.add(new IntegerValue(((BitFieldType) b)
                            .getLength()));
                    continue;
                }
            }
            catch (ClassCastException _)
            {
            }
            size = size.add(fi.sizeof(ctxt).multiply(eight));
        }
        return size.divide(eight);
    }

    public boolean isMember(Context ctxt, Value val)
    {
        // do something like
        // if val.getType() == this
        throw new ComputeError("isMember not implemented");
    }

    void computeBitFieldOffsets(int offset)
    {
        if (bfoComputed)
        {
            return;
        }
        bfoComputed = true;
        Main.debug("computing bfo for " + this + " offset=" + offset);

        Field startField = null;
        for (Iterator i = getFields(); i.hasNext();)
        {
            Field f = (Field) i.next();
            TypeInterface ftype = f.getType();

            if (f.isBitField())
            {
                BitFieldType bftype;
                bftype = (BitFieldType) BuiltinType.getBuiltinType(ftype);
                if (startField == null)
                {
                    startField = f;
                }
                f.bitFieldStart = startField;
                f.bitFieldOffset = offset;
                offset += bftype.length;
            }
            else
            {
                /*
                 * handle anonymous compound types that are embedded at partial
                 * bit offsets
                 */
                if (offset % 8 != 0 && ftype instanceof CompoundType)
                {
                    CompoundType ctype = (CompoundType) ftype;
                    if (ctype.isAnonymous())
                    {
                        ctype.computeBitFieldOffsets(offset);
                    }
                }

                if (startField != null)
                {
                    /*
                     * if (offset % 8 != 0) { throw new InternalError("bitfields
                     * that are not integer " + "multiples of bytes are not
                     * supported, please pad"); }
                     */
                    startField.totalBitFieldLength = offset;
                    startField = null;
                    offset = 0;
                }
            }
        }
        if (startField != null)
        {
            startField.totalBitFieldLength = offset;
            /*
             * if (offset % 8 != 0) { throw new InternalError("bitfields that
             * are not integer " + "multiples of bytes are not supported, please
             * pad"); }
             */
        }
    }

    public Field.LookAhead getLookAhead()
    {
        return getField(0).getLookAhead();
    }
}
