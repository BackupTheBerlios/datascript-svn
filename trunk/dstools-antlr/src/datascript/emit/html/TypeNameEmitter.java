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
package datascript.emit.html;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.EnumType;
import datascript.ast.IntegerType;
import datascript.ast.StdIntegerType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.emit.StringUtil;

/**
 * @author HWellmann
 * 
 */
public class TypeNameEmitter
{
    public TypeNameEmitter()
    {
    }

    public String getHyperlinkedTypeName(TypeInterface t)
    {
        return getTypeName(t, true);
    }
    
    public String getTypeName(TypeInterface t)
    {
        return getTypeName(t, false);
    }
    
    
    public String getTypeName(TypeInterface t, boolean hyperlinked)
    {
        String result = null;
        t = TypeReference.resolveType(t);
        if (t instanceof StdIntegerType)
        {
            result = getTypeName((StdIntegerType) t);
        }
        else if (t instanceof BitFieldType)
        {
            result = getTypeName((BitFieldType) t);
        }
        else if (t instanceof CompoundType)
        {
            CompoundType compound = (CompoundType) t;
            result = wrap(compound.getName(), hyperlinked);
        }
        else if (t instanceof EnumType)
        {
            EnumType enumeration = (EnumType) t;
            result = wrap(enumeration.getName(), hyperlinked);
        }
        else if (t instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation)t;
            CompoundType compound = (CompoundType)inst.getBaseType();
            result = wrap(compound.getName(), hyperlinked);
        }
        else if (t instanceof ArrayType)
        {
            result = getTypeName((ArrayType) t);
            
        }
        else
        {
            result = "/* " + t.toString() + "*/";
        }
        return result;
    }
    
    private String wrap(String name, boolean hyperlinked)
    {
        if (!hyperlinked)
        {
            return name;
        }
        else
        {
            StringBuilder buffer = new StringBuilder("<a href=\"#");
            buffer.append(name);
            buffer.append("\">");
            buffer.append(name);
            buffer.append("</a>");
            return buffer.toString();
        }
    }
    
    private String getTypeName(StdIntegerType t)
    {
        switch (t.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                return "int8";

            case DataScriptParserTokenTypes.UINT8:
                return "uint8";
            case DataScriptParserTokenTypes.INT16:
                return "int16";

            case DataScriptParserTokenTypes.UINT16:
                return "uint16";
            case DataScriptParserTokenTypes.INT32:
                return "int32";

            case DataScriptParserTokenTypes.UINT32:
                return "uint32";
            case DataScriptParserTokenTypes.INT64:
                return "int64";

            case DataScriptParserTokenTypes.UINT64:
                return "uint64";

            default:
                throw new IllegalArgumentException();
        }
    }

    private String getTypeName(BitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "bit<?>";
        else
            return "bit<" + length + ">";
    }
    
    private String getTypeName(ArrayType array)
    {
        TypeInterface elType = array.getElementType();
        String elTypeName = getTypeName(elType);
        return elTypeName;
    }

}
