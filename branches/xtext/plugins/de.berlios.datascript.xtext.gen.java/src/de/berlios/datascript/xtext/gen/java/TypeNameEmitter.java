/* BSD License
 *
 * Copyright (c) 2006-2009, Harald Wellmann, Harman/Becker Automotive Systems
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

package de.berlios.datascript.xtext.gen.java;

import java.util.HashMap;
import java.util.Map;

import de.berlios.datascript.dataScript.ArrayType;
import de.berlios.datascript.dataScript.ComplexType;
import de.berlios.datascript.dataScript.IntegerType;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.dataScript.TypeReference;
import de.berlios.datascript.validation.BuiltInTypes;
import de.berlios.datascript.validation.TypeResolver;

/**
 * @author hwe
 *
 */
public class TypeNameEmitter
{
    private static Map<IntegerType, String> integerTypeNames; 
    
    static
    {
        integerTypeNames = new HashMap<IntegerType, String>();
        integerTypeNames.put(BuiltInTypes.INT8,   "Byte");
        integerTypeNames.put(BuiltInTypes.INT16,  "Short");
        integerTypeNames.put(BuiltInTypes.INT32,  "Integer");
        integerTypeNames.put(BuiltInTypes.INT64,  "Long");
        integerTypeNames.put(BuiltInTypes.UINT8,  "Short");
        integerTypeNames.put(BuiltInTypes.UINT16, "Integer");
        integerTypeNames.put(BuiltInTypes.UINT32, "Long");
        integerTypeNames.put(BuiltInTypes.UINT64, "BigInteger");
    }
    
    
    public static String emit(TypeReference ref)
    {
        Type type = TypeResolver.resolve(ref);
        return emit(type);
    }
    
    private static String emit(Type type)
    {
        String name = null;
        if (BuiltInTypes.isInteger(type))
        {
            name = emitIntegerType(type);
        }
        else if (BuiltInTypes.isString(type))
        {
            name = "String";
        }
        else if (type instanceof ComplexType)
        {
            ComplexType complex = (ComplexType) type;
            name = complex.getName();
        }
        else if (type instanceof ArrayType)
        {
            ArrayType array = (ArrayType) type;
            name = emitArrayType(array);
        }
        else
        {
            throw new IllegalStateException(type.toString());
        }
        return name;
    }

    /**
     * @param array
     * @return
     */
    private static String emitArrayType(ArrayType array)
    {
        Type elementType = array.getElementType();
        String result = String.format("List<%s>", emit(elementType));
        return result;
    }

    /**
     * @param type
     * @return
     */
    private static String emitIntegerType(Type type)
    {
        String result = integerTypeNames.get(type);
        return result;
    }
}
