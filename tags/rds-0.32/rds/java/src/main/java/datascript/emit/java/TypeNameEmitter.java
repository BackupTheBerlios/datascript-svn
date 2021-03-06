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

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.EnumType;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.SignedBitFieldType;
import datascript.ast.StdIntegerType;
import datascript.ast.StringType;
import datascript.ast.Subtype;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;

/**
 * @author HWellmann
 * 
 */
public class TypeNameEmitter
{
    public TypeNameEmitter()
    {
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
        else if (t instanceof Subtype)
        {
            return isBuiltinType(((Subtype)t).getBaseType());
        }
        
        return false;
    }


    public static String getTypeName(TypeInterface t)
    {
        String result = null;
        TypeInterface baseType = TypeReference.getBaseType(t);

        if (baseType instanceof StdIntegerType)
        {
            result = getTypeName((StdIntegerType) baseType);
        }
        else if (baseType instanceof BitFieldType)
        {
            result = getTypeName((BitFieldType) baseType);
        }
        else if (baseType instanceof CompoundType)
        {
            result = getTypeName((CompoundType) baseType);
        }
        else if (baseType instanceof EnumType)
        {
            EnumType enumeration = (EnumType) baseType;
            result = enumeration.getName();
        }
        else if (baseType instanceof TypeInstantiation)
        {
            result = getTypeName((TypeInstantiation)baseType);
        }
        else if (baseType instanceof ArrayType)
        {
            result = getTypeName((ArrayType) baseType);            
        }
        else if (baseType instanceof Subtype)
        {
            TypeInterface base = ((Subtype)baseType).getBaseType();
//            base = TypeReference.getBaseType(base);
            result = getTypeName(base);            
        }
        else if (baseType instanceof StringType)
        {
            result = "String";            
        }
        else
        {
            throw new InternalError("unhandled type = " + baseType.toString());
        }
        return result;
    }


    public static String getTypeName(Field field)
    {
        TypeInterface type = field.getFieldType();
        type = TypeReference.getBaseType(type);

        if (field.getOptionalClause() != null)
        {            
            if (type instanceof StdIntegerType)
            {
                return getNullableTypeName((StdIntegerType) type);
            }
            else if (type instanceof BitFieldType)
            {
                return getNullableTypeName((BitFieldType) type);
            }
        }
        return getTypeName(type);
    }


    private static String getTypeName(StdIntegerType t)
    {
        switch (t.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                return "byte";

            case DataScriptParserTokenTypes.UINT8:
            case DataScriptParserTokenTypes.INT16:
                return "short";

            case DataScriptParserTokenTypes.UINT16:
            case DataScriptParserTokenTypes.INT32:
                return "int";

            case DataScriptParserTokenTypes.UINT32:
            case DataScriptParserTokenTypes.INT64:
                return "long";

            case DataScriptParserTokenTypes.UINT64:
                return "BigInteger";

            default:
                throw new UnsupportedOperationException();
        }
    }


    private static String getNullableTypeName(StdIntegerType t)
    {
        switch (t.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                return "Byte";

            case DataScriptParserTokenTypes.UINT8:
            case DataScriptParserTokenTypes.INT16:
                return "Short";

            case DataScriptParserTokenTypes.UINT16:
            case DataScriptParserTokenTypes.INT32:
                return "Integer";

            case DataScriptParserTokenTypes.UINT32:
            case DataScriptParserTokenTypes.INT64:
                return "Long";

            case DataScriptParserTokenTypes.UINT64:
                return "BigInteger";

            default:
                throw new UnsupportedOperationException();
        }
    }


    private static String getTypeName(BitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "BigInteger";
        
		if (t.isSigned()) 
		{
			if (length <= 8)
				return "byte";
			if (length <= 16)
				return "short";
			if (length <= 32)
				return "int";
			if (length <= 64)
				return "long";
		} 
		else 
		{
			if (length < 8)
				return "byte";
			if (length < 16)
				return "short";
			if (length < 32)
				return "int";
			if (length < 64)
				return "long";
		}
		return "BigInteger";
    }


    private static String getNullableTypeName(BitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "BigInteger";
        
        if (t.isSigned())
        {
            if (length <= 8)
            	return "Byte";
            if (length <= 16)
            	return "Short";
            if (length <= 32)
            	return "Integer";
            if (length <= 64)
            	return "Long";        	
        }
        else
        {
            if (length < 8)
            	return "Byte";
            if (length < 16)
            	return "Short";
            if (length < 32)
            	return "Integer";
            if (length < 64)
            	return "Long";
        }
        return "BigInteger";
    }


    private static String getTypeName(CompoundType compound)
    {
        return compound.getName();                
    }


    private static String getTypeName(TypeInstantiation inst)
    {
        CompoundType compound = inst.getBaseType();
        return compound.getName();        
    }


    private static String getTypeName(ArrayType array)
    {
        TypeInterface elType = array.getElementType();
        if (elType instanceof IntegerType)
        {
            IntegerType intType = (IntegerType) elType;
            switch (intType.getType())
            {
                case DataScriptParserTokenTypes.INT8:
                    return "ByteArray";

                case DataScriptParserTokenTypes.UINT8:
                    return "UnsignedByteArray";

                case DataScriptParserTokenTypes.INT16:
                    return "ShortArray";

                case DataScriptParserTokenTypes.UINT16:
                    return "UnsignedShortArray";

                case DataScriptParserTokenTypes.INT32:
                    return "IntArray";

                case DataScriptParserTokenTypes.UINT32:
                    return "UnsignedIntArray";

                case DataScriptParserTokenTypes.INT64:
                    return "LongArray";

                case DataScriptParserTokenTypes.BIT:
                    return "BitFieldArray";

                default:
                    throw new UnsupportedOperationException();
            }
        }
        else if (elType instanceof StringType)
        {
            return "StringArray";
        }
        String elTypeName = getTypeName(elType);
        return "ObjectArray<" + elTypeName +  ">";        
    }


    public static String getClassName(TypeInterface t)
    {
        String result = null;
        TypeInterface baseType = TypeReference.getBaseType(t);

        if (baseType instanceof StdIntegerType)
        {
            result = getClassName((StdIntegerType) baseType);
        }
        else if (baseType instanceof SignedBitFieldType)
        {
            result = getClassName((SignedBitFieldType) baseType);
        }
        else if (baseType instanceof BitFieldType)
        {
            result = getClassName((BitFieldType) baseType);
        }
        else
        {
            result = getTypeName(baseType);
        }
        return result;
    }


    private static String getClassName(StdIntegerType t)
    {
        switch (t.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                return "Byte";

            case DataScriptParserTokenTypes.UINT8:
            case DataScriptParserTokenTypes.INT16:
                return "Short";

            case DataScriptParserTokenTypes.UINT16:
            case DataScriptParserTokenTypes.INT32:
                return "Integer";

            case DataScriptParserTokenTypes.UINT32:
            case DataScriptParserTokenTypes.INT64:
                return "Long";

            case DataScriptParserTokenTypes.UINT64:
                return "BigInteger";

            default:
                throw new UnsupportedOperationException();
        }        
    }


    private static String getClassName(BitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "BigInteger";
        else if (length < 8)
            return "Byte";
        else if (length < 16)
            return "Short";
        else if (length < 32)
            return "Integer";
        else if (length < 64)
            return "Long";
        else
            return "BigInteger";
    }

    private static String getClassName(SignedBitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "BigInteger";
        else if (length <= 8)
            return "Byte";
        else if (length <= 16)
            return "Short";
        else if (length <= 32)
            return "Integer";
        else if (length <= 64)
            return "Long";
        else
            return "BigInteger";
    }
}
