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

import datascript.parser.DSConstants;
import java.math.BigInteger;

public class BuiltinType extends IntegerType implements TypeInterface
{
    public BuiltinType(int kind)
    {
        this.kind = kind;
    }

    protected int kind;

    int getKind()
    {
        return kind;
    }

    boolean isSigned()
    {
        switch (kind)
        {
            case DSConstants.INT8:
            case DSConstants.INT16:
            case DSConstants.LEINT16:
            case DSConstants.INT32:
            case DSConstants.LEINT32:
            case DSConstants.INT64:
            case DSConstants.LEINT64:
                return true;

            default:
                return false;
        }
    }

    BigInteger getLowerBound()
    {
        return lowerbounds[kind];
    }

    BigInteger getUpperBound()
    {
        return upperbounds[kind];
    }

    public String toString()
    {
        // remove double quotes from "uint8"
        int l = DSConstants.tokenImage[kind].length();
        return DSConstants.tokenImage[kind].substring(1, l - 1);
    }

    public IntegerValue sizeof(Context ctxt)
    {
        switch (kind)
        {
            case DSConstants.UINT8:
            case DSConstants.INT8:
                return new IntegerValue(1);
            case DSConstants.UINT16:
            case DSConstants.INT16:
            case DSConstants.LEUINT16:
            case DSConstants.LEINT16:
                return new IntegerValue(2);
            case DSConstants.UINT32:
            case DSConstants.INT32:
            case DSConstants.LEUINT32:
            case DSConstants.LEINT32:
                return new IntegerValue(4);
            case DSConstants.UINT64:
            case DSConstants.INT64:
            case DSConstants.LEUINT64:
            case DSConstants.LEINT64:
                return new IntegerValue(8);
            default:
                throw new ComputeError("unknown type in sizeof");
        }
    }

    private static BigInteger lowerbounds[];
    private static BigInteger upperbounds[];
    private static BuiltinType builtinTypes[];

    static
    {
        int n = DSConstants.tokenImage.length;
        lowerbounds = new BigInteger[n];
        upperbounds = new BigInteger[n];
        lowerbounds[DSConstants.UINT8] = BigInteger.ZERO;
        lowerbounds[DSConstants.UINT16] = BigInteger.ZERO;
        lowerbounds[DSConstants.UINT32] = BigInteger.ZERO;
        lowerbounds[DSConstants.UINT64] = BigInteger.ZERO;
        lowerbounds[DSConstants.LEUINT16] = BigInteger.ZERO;
        lowerbounds[DSConstants.LEUINT32] = BigInteger.ZERO;
        lowerbounds[DSConstants.LEUINT64] = BigInteger.ZERO;

        upperbounds[DSConstants.UINT8] = BigInteger.ONE.shiftLeft(8);
        upperbounds[DSConstants.UINT16] = BigInteger.ONE.shiftLeft(16);
        upperbounds[DSConstants.UINT32] = BigInteger.ONE.shiftLeft(32);
        upperbounds[DSConstants.UINT64] = BigInteger.ONE.shiftLeft(64);
        upperbounds[DSConstants.LEUINT16] = BigInteger.ONE.shiftLeft(16);
        upperbounds[DSConstants.LEUINT32] = BigInteger.ONE.shiftLeft(32);
        upperbounds[DSConstants.LEUINT64] = BigInteger.ONE.shiftLeft(64);
        upperbounds[DSConstants.INT8] = BigInteger.ONE.shiftLeft(7).subtract(
                BigInteger.ONE);
        upperbounds[DSConstants.LEINT16] = upperbounds[DSConstants.INT16] = BigInteger.ONE
                .shiftLeft(15).subtract(BigInteger.ONE);
        upperbounds[DSConstants.LEINT32] = upperbounds[DSConstants.INT32] = BigInteger.ONE
                .shiftLeft(31).subtract(BigInteger.ONE);
        upperbounds[DSConstants.LEINT64] = upperbounds[DSConstants.INT64] = BigInteger.ONE
                .shiftLeft(63).subtract(BigInteger.ONE);
        lowerbounds[DSConstants.INT8] = upperbounds[DSConstants.INT8].not();
        lowerbounds[DSConstants.INT16] = upperbounds[DSConstants.INT16].not();
        lowerbounds[DSConstants.INT32] = upperbounds[DSConstants.INT32].not();
        lowerbounds[DSConstants.INT64] = upperbounds[DSConstants.INT64].not();
        lowerbounds[DSConstants.LEINT16] = upperbounds[DSConstants.INT16].not();
        lowerbounds[DSConstants.LEINT32] = upperbounds[DSConstants.INT32].not();
        lowerbounds[DSConstants.LEINT64] = upperbounds[DSConstants.INT64].not();
        builtinTypes = new BuiltinType[n];
        builtinTypes[DSConstants.INT8] = new BuiltinType(DSConstants.INT8);
        builtinTypes[DSConstants.INT16] = new BuiltinType(DSConstants.INT16);
        builtinTypes[DSConstants.INT32] = new BuiltinType(DSConstants.INT32);
        builtinTypes[DSConstants.INT64] = new BuiltinType(DSConstants.INT64);
        builtinTypes[DSConstants.LEINT16] = new BuiltinType(DSConstants.LEINT16);
        builtinTypes[DSConstants.LEINT32] = new BuiltinType(DSConstants.LEINT32);
        builtinTypes[DSConstants.LEINT64] = new BuiltinType(DSConstants.LEINT64);
        builtinTypes[DSConstants.UINT8] = new BuiltinType(DSConstants.UINT8);
        builtinTypes[DSConstants.UINT16] = new BuiltinType(DSConstants.UINT16);
        builtinTypes[DSConstants.UINT32] = new BuiltinType(DSConstants.UINT32);
        builtinTypes[DSConstants.UINT64] = new BuiltinType(DSConstants.UINT64);
        builtinTypes[DSConstants.LEUINT16] = new BuiltinType(
                DSConstants.LEUINT16);
        builtinTypes[DSConstants.LEUINT32] = new BuiltinType(
                DSConstants.LEUINT32);
        builtinTypes[DSConstants.LEUINT64] = new BuiltinType(
                DSConstants.LEUINT64);
    }

    static BuiltinType getTypeByTokenKind(int k)
    {
        return builtinTypes[k];
    }

    private static void p(int k)
    {
        System.out.println(lowerbounds[k] + " <= " + DSConstants.tokenImage[k]
                + " < " + upperbounds[k]);
    }

    /**
     * for builtin types, membership test amounts to testing whether it's an
     * integer (via integerValue()) and then a simple range comparison
     */
    public boolean isMember(Context ctxt, Value val)
    {
        try
        {
            return (lowerbounds[kind].compareTo(val.integerValue()) != 1 && val
                    .integerValue().compareTo(upperbounds[kind]) == -1);
        }
        catch (ComputeError _)
        {
            return (false);
        }
    }

    public Value castFrom(Value val)
    {
        if (isMember(null, val))
        {
            return val;
        }
        throw new CastError("cannot cast " + val + " to " + this);
    }

    static BuiltinType getBuiltinType(TypeInterface ftype)
    {
        BuiltinType btype;
        if (ftype instanceof BuiltinType)
        {
            btype = (BuiltinType) ftype;
        }
        else
        {
            btype = (BuiltinType) ((SetType) ftype).getType();
        }
        return btype;
    }
}
