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


/**
 * This class represents a parameter inside the body of a condition definition
 * or for a compound type
 */
public class Parameter
{
    private String name;

    private TypeInterface type;


    public Parameter(String name, TypeInterface type)
    {
        this.name = name;
        this.type = type;
    }


    public TypeInterface getType()
    {
        return (type);
    }


    public String getName()
    {
        return (name);
    }


    public String getCanonicalTypeName()
    {
        return type.getClass().getCanonicalName();
    }


    public int getBitFieldLength()
    {
        if (type instanceof BitFieldType)
            return ((BitFieldType)type).getLength();
        throw new RuntimeException("type of field " + name + "is not a BitFieldType");
    }


    public boolean getIsCompoundType()
    {
        return type instanceof CompoundType || type instanceof TypeInstantiation;
    }


    public boolean getIsSimpleIntegerType()
    {
        boolean result = false;
        if (type instanceof TypeReference)
        {
            type = TypeReference.resolveType(type);
        }
        if (type instanceof ArrayType)
        {
            type = TypeReference.resolveType(((ArrayType)type).getElementType());
        }
        if (type instanceof Subtype)
        {
            type = TypeReference.getBaseType(type);
        }
        if (type instanceof StdIntegerType)
        {
            result = ((StdIntegerType)type).getType() != datascript.antlr.DataScriptParserTokenTypes.UINT64;            
        }
        else if (type instanceof BitFieldType)
        {
            BitFieldType bitField = (BitFieldType) type;
            result = 0 < bitField.getLength() && bitField.getLength() < 64;
        }
        return result;
    }
}
