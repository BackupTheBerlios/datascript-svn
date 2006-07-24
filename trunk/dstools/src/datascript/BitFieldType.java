/*
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

import java.math.BigInteger;

import datascript.parser.DSConstants;
import datascript.syntaxtree.Node;

public class BitFieldType extends BuiltinType
{
    /** 
     * Number of bits of this bitfield. 
     * When length is known at compile time, this attribute is set to a value
     * > 0. Otherwise, length is set to -1, and the runtime length is
     * indicated by the expression stored in varLength. 
     */
    int length;

    /**
     * Expression indicating the run-time length of this bitfield.
     * If this is null, length must be set to a value > 0. Otherwise,
     * length == -1.
     */
    Node varLength;

    BigInteger lowerBound, upperBound;

    int getLength()
    {
        /** @TODO handle variable length! */
        return length;
    }
    
    public boolean isVariable()
    {
        return varLength != null;
    }

    /**
     * BitFields are always unsigned
     */
    BitFieldType(int length, Node lengthExpr)
    {
        super(DSConstants.BIT);
        this.length = length;
        this.varLength = lengthExpr;
        if (lengthExpr == null && length <= 0)
        {
            throw new SemanticError("bitfield length must be positive");
        }
        upperBound = BigInteger.ONE.shiftLeft(length);
        lowerBound = BigInteger.ZERO;
        if (length < 0)
        {
            // variable length; do nothing 
            // the default DSConstants.BIT is what we want
        }
        else if (length <= 7)
        {
            this.kind = DSConstants.INT8;
        }
        else if (length <= 15)
        {
            this.kind = DSConstants.INT16;
        }
        else if (length <= 31)
        {
            this.kind = DSConstants.INT32;
        }
        else if (length <= 63)
        {
            this.kind = DSConstants.INT64;
        }
    }

    public IntegerValue sizeof(Context ctxt)
    {
        /** @TODO handle variable length! */
        if (length % 8 == 0)
            return new IntegerValue(length / 8);
        throw new ComputeError(
                "bitfield length not an integral number of bytes");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        /** @TODO handle variable length! */
        try
        {
            return (lowerBound.compareTo(val.integerValue()) != 1 && val
                    .integerValue().compareTo(upperBound) == -1);
        }
        catch (ComputeError _)
        {
            return (false);
        }
    }

    public String toString()
    {
        return "BitField"; 
        // We no longer append "/* :" + length + " */", since this causes
        // a nested comment with an array of bitfield.
    }
}
