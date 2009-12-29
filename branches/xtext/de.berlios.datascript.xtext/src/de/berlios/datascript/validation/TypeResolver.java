package de.berlios.datascript.validation;

import java.util.HashMap;
import java.util.Map;

import de.berlios.datascript.dataScript.ComplexType;
import de.berlios.datascript.dataScript.Constant;
import de.berlios.datascript.dataScript.EnumMember;
import de.berlios.datascript.dataScript.EnumType;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.Function;
import de.berlios.datascript.dataScript.Parameter;
import de.berlios.datascript.dataScript.SimpleType;
import de.berlios.datascript.dataScript.SqlFieldDefinition;
import de.berlios.datascript.dataScript.Subtype;
import de.berlios.datascript.dataScript.Type;
import de.berlios.datascript.dataScript.TypeReference;
import de.berlios.datascript.dataScript.Value;

public class TypeResolver
{
    static Map<String, Type> simpleTypeMap = new HashMap<String, Type>();
    
    static
    {
        simpleTypeMap.put("int8", BuiltInTypes.INT8);
        simpleTypeMap.put("int16", BuiltInTypes.INT16);
        simpleTypeMap.put("int32", BuiltInTypes.INT32);
        simpleTypeMap.put("int64", BuiltInTypes.INT64);
        simpleTypeMap.put("uint8", BuiltInTypes.UINT8);
        simpleTypeMap.put("uint16", BuiltInTypes.UINT16);
        simpleTypeMap.put("uint32", BuiltInTypes.UINT32);
        simpleTypeMap.put("uint64", BuiltInTypes.UINT64);
        simpleTypeMap.put("int", BuiltInTypes.INT_N);
        simpleTypeMap.put("bit", BuiltInTypes.UINT_N);
        simpleTypeMap.put("string", BuiltInTypes.STRING);
    }
    
    static public Type resolve(Type t)
    {
        Type type = t;
        while (true)
        {
            if (type instanceof Subtype)
                type = ((Subtype) type).getType().getRef();
            else if (type instanceof TypeReference)
                type = ((TypeReference) type).getRef();
            else 
                break;
        }
        return type;
    }

    static public Type resolve(TypeReference ref)
    {
        Type type = null;
        if (ref instanceof SimpleType)
        {
            SimpleType simple = (SimpleType) ref;
            type = simpleTypeMap.get(simple.getBuiltIn());
        }
        else
        {
            type = resolve(ref.getRef());
        }
        return type;
    }
    
    static public Type getType(Value ref)
    {
        Type type = null;
        if (ref instanceof EnumMember)
        {
            EnumMember member = (EnumMember) ref;
            type = (EnumType) member.eContainer();
        }
        else if (ref instanceof Field)
        {
            Field field = (Field) ref;
            type = TypeResolver.resolve(field.getType());
        }
        else if (ref instanceof Parameter)
        {
            Parameter parameter = (Parameter) ref;
            type = TypeResolver.resolve(parameter.getType());
        }
        else if (ref instanceof SqlFieldDefinition)
        {
            SqlFieldDefinition field = (SqlFieldDefinition) ref;
            type = TypeResolver.resolve(field.getType());
        }
        else if (ref instanceof Constant)
        {
            Constant constant = (Constant) ref;
            type = TypeResolver.resolve(constant.getType());
        }
        else if (ref instanceof ComplexType)
        {
            ComplexType complex = (ComplexType) ref;
            type = TypeResolver.resolve(complex);
        }
        else if (ref instanceof Function)
        {
            Function function = (Function) ref;
            Type resultType = resolve(function.getType());
            type = ImplicitTypes.createFunctionType(resultType);
        }
        
        return type;
    }
}
