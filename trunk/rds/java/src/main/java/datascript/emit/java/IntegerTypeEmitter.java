/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Henrik Wedekind Harman/Becker Automotive Systems
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


package datascript.emit.java;


import java.io.PrintWriter;
import java.util.Calendar;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.BitFieldType;
import datascript.ast.Expression;
import datascript.ast.IntegerType;
import datascript.ast.SignedBitFieldType;
import datascript.ast.Value;



abstract public class IntegerTypeEmitter
{
    protected JavaDefaultEmitter global;
    protected ExpressionEmitter exprEmitter = new ExpressionEmitter();
    protected PrintWriter writer;


    public IntegerTypeEmitter(JavaDefaultEmitter j)
    {
        global = j;
    }


    public JavaDefaultEmitter getGlobal()
    {
        return global;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    protected void readIntegerValue(StringBuilder buffer, IntegerType type)
    {
        String methodSuffix;
        String cast = "";
        String arg = "";
        switch (type.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                methodSuffix = "Byte";
                break;

            case DataScriptParserTokenTypes.INT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.INT32:
                methodSuffix = "Int";
                break;

            case DataScriptParserTokenTypes.INT64:
                methodSuffix = "Long";
                break;

            case DataScriptParserTokenTypes.UINT8:
                methodSuffix = "UnsignedByte";
                cast = "(short) ";
                break;

            case DataScriptParserTokenTypes.UINT16:
                methodSuffix = "UnsignedShort";
                break;

            case DataScriptParserTokenTypes.UINT32:
                methodSuffix = "UnsignedInt";
                break;

            case DataScriptParserTokenTypes.UINT64:
                methodSuffix = "BigInteger";
                arg = "64";
                break;

            case DataScriptParserTokenTypes.BIT:
                Expression lengthExpr = ((BitFieldType) type)
                        .getLengthExpression();
                Value lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "BigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length < 64)
                    {
                        methodSuffix = "Bits";
                        cast = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "BigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            case DataScriptParserTokenTypes.INT:
                lengthExpr = ((SignedBitFieldType) type)
                        .getLengthExpression();
                lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "SignedBigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length <= 64)
                    {
                        methodSuffix = "SignedBits";
                        cast = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "SignedBigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            default:
                throw new InternalError("unhandled type = " + type.getType());
        }
        buffer.append(cast);
        buffer.append("__in.read");
        buffer.append(methodSuffix);
        buffer.append("(");
        buffer.append(arg);
        buffer.append(")");
    }




    protected void writeIntegerValue(StringBuilder buffer, String value, IntegerType type)
    {
        String methodSuffix;
        String castPrefix = "";
        String castSuffix = "";
        String arg = "";
        switch (type.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                methodSuffix = "Byte";
                break;

            case DataScriptParserTokenTypes.INT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.INT32:
                methodSuffix = "Int";
                break;

            case DataScriptParserTokenTypes.INT64:
                methodSuffix = "Long";
                break;

            case DataScriptParserTokenTypes.UINT8:
                methodSuffix = "Byte";
                castPrefix = "new Long(";
                castSuffix = ").shortValue()";
                break;

            case DataScriptParserTokenTypes.UINT16:
                methodSuffix = "Short";
                break;

            case DataScriptParserTokenTypes.UINT32:
                methodSuffix = "UnsignedInt";
                castPrefix = "new Long(";
                castSuffix = ").intValue()";
                break;

            case DataScriptParserTokenTypes.UINT64:
                methodSuffix = "BigInteger";
                arg = "64";
                break;

            case DataScriptParserTokenTypes.BIT:
                Expression lengthExpr = ((BitFieldType) type)
                        .getLengthExpression();
                Value lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "BigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length < 64)
                    {
                        methodSuffix = "Bits";
                        castPrefix = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "BigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            case DataScriptParserTokenTypes.INT:
                lengthExpr = ((BitFieldType) type)
                        .getLengthExpression();
                lengthValue = lengthExpr.getValue();
                if (lengthValue == null)
                {
                    methodSuffix = "BigInteger";
                }
                else
                {
                    int length = lengthValue.integerValue().intValue();
                    if (length <= 64)
                    {
                        methodSuffix = "Bits";
                        castPrefix = "(" + TypeNameEmitter.getTypeName(type) + ") ";
                    }
                    else
                    {
                        methodSuffix = "BigInteger";
                    }
                }
                arg = exprEmitter.emit(lengthExpr);
                break;

            default:
                throw new InternalError("unhandled type = " + type.getType());
        }
        buffer.append("__out.write");
        buffer.append(methodSuffix);
        buffer.append("(");
        buffer.append(castPrefix);
        buffer.append(value);
        buffer.append(castSuffix);
        if (arg.length() != 0)
        {
            buffer.append(", ");
            buffer.append(arg);
        }
        buffer.append(");");
    }


    /**** interface to freemarker FileHeader.inc template ****/

    public String getRdsVersion()
    {
        return global.getRdsVersion();
    }


    /**
     * Calculates the actual time and returns a formattet string that follow the 
     * ISO 8601 standard (i.e. "2007-13-11T12:08:56.235-0700")
     * @return      actual time as a ISO 8601 formattet string
     */
    public String getTimeStamp()
    {
        return String.format("%1$tFT%1$tT.%1$tL%1$tz", Calendar.getInstance());
    }


    public String getPackageName()
    {
        return global.getPackageName();
    }


    public String getRootPackageName()
    {
        return datascript.ast.Package.getRoot().getPackageName();
    }


    public boolean getEqualsCanThrowExceptions()
    {
        return global.getThrowsException();
    }


    public String getPackageImports()
    {
        return global.getPackageImports();
    }
}
