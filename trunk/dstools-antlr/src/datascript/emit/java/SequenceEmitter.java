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
package datascript.emit.java;

import java.io.PrintStream;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.SequenceType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.Value;
import datascript.jet.java.ArrayRead;
import datascript.jet.java.SequenceRead;
import datascript.jet.java.SequenceBegin;
import datascript.jet.java.SequenceEnd;

public class SequenceEmitter
{
    private static String nl = System.getProperties().getProperty("line.separator");
    private SequenceType seq;
    private JavaEmitter global;
    private SequenceFieldEmitter fieldEmitter;
    private TypeNameEmitter typeNameEmitter;
    private ExpressionEmitter exprEmitter = new ExpressionEmitter();
    private SequenceBegin beginTmpl = new SequenceBegin();
    private SequenceEnd endTmpl = new SequenceEnd();
    private SequenceRead readTmpl = new SequenceRead();
    private ArrayRead arrayTmpl = new ArrayRead();
    private StringBuilder buffer;
    private PrintStream out;
    
    public SequenceEmitter(JavaEmitter j)
    {
        this.global = j;
        this.fieldEmitter = new SequenceFieldEmitter(j);
        this.typeNameEmitter = new TypeNameEmitter(j);
    }
   
    public SequenceType getSequenceType()
    {
        return seq;
    }
    
    public JavaEmitter getGlobal()
    {
        return global;
    }

    public void setOutputStream(PrintStream out)
    {
        this.out = out;
        fieldEmitter.setOutputStream(out);
    }
    
    public void begin(SequenceType s)
    {
        seq = s;
        String result = beginTmpl.generate(this);
        out.print(result);
        
        for (Field field : s.getFields())
        {
            //out.println("    // field "+ field.getName());
            fieldEmitter.emit(field);
        }
        result = readTmpl.generate(this);
        out.print(result);
    }
    
    public void end(SequenceType s)
    {
        String result = endTmpl.generate(this);
        out.print(result);
    }
    
    public void readFields()
    {
        for (Field field : seq.getFields())
        {
            //out.println("    // field "+ field.getName());
            readField(field);
        }
    }
    
    public String readField(Field field)
    {
        buffer = new StringBuilder();
        TypeInterface type = field.getFieldType();
        type = TypeReference.resolveType(type);
        if (type instanceof IntegerType)
        {
            readIntegerField(field, (IntegerType)type);
        }
        else if (type instanceof CompoundType)
        {
            readCompoundField(field, (CompoundType)type);
        }
        else if (type instanceof ArrayType)
        {
            readArrayField(field, (ArrayType)type);
        }
        else if (type instanceof EnumType)
        {
            readEnumField(field, (EnumType)type);
        }
        else if (type instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation)type;
            CompoundType compound = inst.getBaseType();
            readCompoundField(field, compound);
        }
        else
        {
            throw new InternalError("unhandled type: " + type.getClass().getName());
        }
        return buffer.toString();
    }
    
    private void indent()
    {
        buffer.append("                "); // 4*4
    }
    
    private void readIntegerField(Field field, IntegerType type)
    {
        buffer.append(field.getName());
        buffer.append(" = ");
        readIntegerValue(type);
    }
    
    private void readIntegerValue(IntegerType type)
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
                Expression lengthExpr = ((BitFieldType)type).getLengthExpression();
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
                        cast = "(" + typeNameEmitter.getTypeName(type) + ") ";
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
        buffer.append(cast);
        buffer.append("__in.read");
        buffer.append(methodSuffix);
        buffer.append("(");      
        buffer.append(arg);      
        buffer.append(");");
    }
    
    private void readCompoundField(Field field, CompoundType type)
    {
        buffer.append(field.getName());
        buffer.append(" = new ");
        buffer.append(type.getName());
        buffer.append("(__in, __cc);");
    }
    
    private void readArrayField(Field field, ArrayType array)
    {
        String elTypeJavaName = typeNameEmitter.getTypeName(array);
        if (elTypeJavaName.startsWith("ObjectArray"))
        {
            String elTypeName = typeNameEmitter.getTypeName(array.getElementType());
            ArrayEmitter arrayEmitter = new ArrayEmitter(field.getName(), array, 
                    elTypeName);
            String result = arrayTmpl.generate(arrayEmitter);
            buffer.append(result);            
        }
        else
        {
            Expression length = array.getLengthExpression();
            buffer.append(field.getName());
            buffer.append(" = new ");
            buffer.append(elTypeJavaName);
            buffer.append("(__in, ");
            buffer.append(getLengthExpression(length));
            buffer.append(");");
        }
    }
    
    private void readEnumField(Field field, EnumType type)
    {
        IntegerType baseType = (IntegerType) type.getBaseType();
        String baseTypeName = typeNameEmitter.getTypeName(baseType);
        String fname = field.getName();
        buffer.append(baseTypeName);
        buffer.append(" __");
        buffer.append(field.getName());
        buffer.append(" = ");
        readIntegerValue(baseType);
        buffer.append(nl);
        indent();
        buffer.append(fname);
        buffer.append(" = ");
        buffer.append(type.getName());
        buffer.append(".toEnum(__");
        buffer.append(fname);
        buffer.append(");");        
    }
    
    private String getLengthExpression(Expression expr)
    {
/*        
        // TODO handle variable length
        Value value = expr.getValue();
        int length = (value == null) ? 0 : value.integerValue().intValue();
        
        return Integer.toString(length);
*/        
        return exprEmitter.emit(expr);
        
    }
}
