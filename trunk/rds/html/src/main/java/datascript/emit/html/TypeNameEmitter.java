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
import datascript.ast.*;
import datascript.emit.html.ExpressionEmitter;

/**
 * @author HWellmann
 * 
 */
public class TypeNameEmitter
{
    ExpressionEmitter exprEmitter = new ExpressionEmitter();
    
    public TypeNameEmitter()
    {
    }


    public String getLabel(Field f)
    {
        String result = "";
        Expression label = f.getLabel();
        if (label != null)
        {
            result = exprEmitter.emit(label) + ":";
        }
        return result;
    }


    public String getArrayRange(Field f)
    {
        String result = null;
        TypeInterface type = f.getFieldType();

        type = TypeReference.resolveType(type);
        if (type instanceof ArrayType)
        {
            result = "[";
            Expression expr = ((ArrayType)type).getLengthExpression();
            if (expr != null)
            {
                result += exprEmitter.emit(expr);
            }
            result += "]";
        }
        
        return result;
    }

    public String getOptionalClause(Field field)
    {
        String result = "";
        Expression expr = field.getOptionalClause();
        if (expr != null)
        {
            result = " if " + exprEmitter.emit(expr);
        }
        return result;
    }

    public String getConstraint(Field field)
    {
        String result = "";
        Expression expr = field.getCondition();
        if (expr != null)
        {
            result = " : " + exprEmitter.emit(expr);
        }
        else
        {
            expr = field.getInitializer();
            if (expr != null)
            {
                result = " : " + field.getName() + " == " + exprEmitter.emit(expr);
            }
        }

        return result;
    }


    public static boolean isBuiltinType(TypeInterface t)
    {
        if (t instanceof StdIntegerType ||
            t instanceof BitFieldType ||
            t instanceof StringType)
        {
            return true;
        }
        else if (t instanceof ArrayType)
        {
            return isBuiltinType(((ArrayType)t).getElementType());
        }
        
        return false;
    }


    public static String getTypeName(TypeInterface t)
    {
        String result = null;

        //t = TypeReference.resolveType(t);
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
            result = compound.getName();
        }
        else if (t instanceof EnumType)
        {
            EnumType enumeration = (EnumType) t;
            result = enumeration.getName();
        }
        else if (t instanceof Subtype)
        {
            Subtype subtype = (Subtype) t;
            result = subtype.getName();
        }
        else if (t instanceof ConstType)
        {
            ConstType consttype = (ConstType) t;
            result = consttype.getName();
        }
        else if (t instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation)t;
            CompoundType compound = inst.getBaseType();
            result = compound.getName();
        }
        else if (t instanceof ArrayType)
        {
            result = getTypeName(((ArrayType)t).getElementType());
        }
        else if (t instanceof TypeReference)
        {
            TypeReference reference = (TypeReference) t;
            result = reference.getName();
        }
        else
        {
            TypeInterface res = TypeReference.resolveType(t);
            result = res.toString();
        }

        return result;
    }


    private static String getTypeName(StdIntegerType t)
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


    private static String getTypeName(BitFieldType t)
    {
        Expression e = t.getLengthExpression();
        if (e != null)
        {
            ExpressionEmitter emitter = new ExpressionEmitter();
            return "bit&lt;" + emitter.emit(e) + "&gt;";
        }
        
        int length = t.getLength();        
        if (length == 0)
            return "bit&lt;?&gt;";
        else
            return "bit&lt;" + length + "&gt;";
    }

}
