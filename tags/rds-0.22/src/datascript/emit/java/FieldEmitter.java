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

import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.StdIntegerType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.emit.java.TypeNameEmitter;
import freemarker.template.Configuration;



public abstract class FieldEmitter
{
    protected final Field field;
    protected PrintWriter writer;

    private final CompoundEmitter global;
    private TypeInterface type;
    private String optional = null;
    private String constraint = null;
    private String label = null;


    public FieldEmitter(Field f, CompoundEmitter j)
    {
        global = j;
        field = f;
        type = TypeReference.getBaseType(field.getFieldType());
    }


    abstract public void emit(PrintWriter writer, Configuration cfg) throws Exception;


    public String getReadField()
    {
        return getCompoundEmitter().readField(field);
    }


    public String getWriteField()
    {
        return getCompoundEmitter().writeField(field);
    }


    public String getOptionalClause()
    {
        if (optional == null)
        {
            optional = getCompoundEmitter().getOptionalClause(field);
        }
        return optional;
    }


    public String getConstraint()
    {
        if (constraint == null)
        {
            constraint = getCompoundEmitter().getConstraint(field);
        }
        return constraint;
    }


    public String getLabelExpression()
    {
        if (label == null)
        {
            label = getCompoundEmitter().getLabelExpression(field);
        }
        return label;
    }


    public String getCanonicalTypeName()
    {
        return type.getClass().getCanonicalName();
    }


    public String getJavaTypeName()
    {
        return TypeNameEmitter.getTypeName(field);
    }


    public String getTypeName()
    {
        String typeName = datascript.emit.html.TypeNameEmitter.getTypeName(type);
        typeName = typeName.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
        return typeName;
    }


    public String getClassName()
    {
        return TypeNameEmitter.getClassName(field.getFieldType());
    }


    public String getName()
    {
        return field.getName();
    }


    public String getGetterName()
    {
        return AccessorNameEmitter.getGetterName(field);
    }


    public String getSetterName()
    {
        return AccessorNameEmitter.getSetterName(field);
    }


    public String getIndicatorName()
    {
        return AccessorNameEmitter.getIndicatorName(field);
    }


    public int getBitFieldLength()
    {
        if (type instanceof BitFieldType)
            return ((BitFieldType)type).getLength();
        throw new RuntimeException("type of field '" + field.getName() + "' is not a BitFieldType");
    }


    public boolean getIsUINT64()
    {
        if (type instanceof StdIntegerType)
            return ((StdIntegerType)type).getType() == datascript.antlr.DataScriptParserTokenTypes.UINT64;
        throw new RuntimeException("type of field '" + field.getName() + "' is not a StdIntegerType");
    }


    public boolean getIsIntegerType()
    {
        return type instanceof StdIntegerType;
    }


    public boolean getIsSimple()
    {
        if (field.getOptionalClause() != null)
        {
            return false;
        }
        boolean result = false;
        if (type instanceof StdIntegerType)
        {
            result = (((StdIntegerType)type).getType() != datascript.antlr.DataScriptParserTokenTypes.UINT64);
        }
        else if (type instanceof BitFieldType)
        {
            BitFieldType bitField = (BitFieldType) type;
            result = 0 < bitField.getLength() && bitField.getLength() < 64;
        }
        return result;
    }


    public long getMinVal()
    {
    	if (type instanceof BitFieldType)
    	{
            return 0;
    	}
        if (type instanceof StdIntegerType)
        {
        	StdIntegerType integerType = (StdIntegerType) type;
        	return integerType.getLowerBound().longValue();
        }
    	throw new RuntimeException("type of field '" + field.getName() + "' is not a simple type");
    }


    public long getMaxVal()
    {
    	if (type instanceof BitFieldType)
    	{
            BitFieldType bitField = (BitFieldType) type;
            return (1 << bitField.getLength()) -1;
    	}
        if (type instanceof StdIntegerType)
        {
        	StdIntegerType integerType = (StdIntegerType) type;
        	return integerType.getUpperBound().longValue();
        }
    	throw new RuntimeException("type of field '" + field.getName() + "' is not a simple type");
    }


    public CompoundEmitter getCompoundEmitter()
    {
        return global;
    }


    public void setWriter(PrintWriter writer)
    {
        this.writer = writer;
    }


    /*
     * public String getTypeName()
     * {
     *     TypeInterface type = field.getFieldType();
     *     type = TypeReference.getBaseType(type);
     *     return global.getTypeName(type);
     * }
     */


    public Field getField()
    {
        return field;
    }


    public boolean getEqualsCanThrowExceptions()
    {
        return global.getEqualsCanThrowExceptions();
    }
    
    public String getElementType()
    {
        if (field.getFieldType() instanceof ArrayType)
        {
            ArrayType arrayType = (ArrayType) field.getFieldType();
            TypeInterface elType = arrayType.getElementType();
            if (elType instanceof CompoundType || 
                elType instanceof EnumType ||
                elType instanceof TypeInstantiation)
            {
                return TypeNameEmitter.getTypeName(elType);
            }
        }
        return null;
    }
}
