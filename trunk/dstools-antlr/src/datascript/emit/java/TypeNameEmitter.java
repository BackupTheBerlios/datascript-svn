/**
 * 
 */
package datascript.emit.java;

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
    private JavaEmitter global;

    public TypeNameEmitter(JavaEmitter j)
    {
        this.global = j;
    }

    public String getTypeName(TypeInterface t)
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
            result = compound.getName();
        }
        else if (t instanceof EnumType)
        {
            EnumType enumeration = (EnumType) t;
            result = enumeration.getName();
        }
        else if (t instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation)t;
            CompoundType compound = (CompoundType)inst.getBaseType();
            result = compound.getName();
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

    private String getTypeName(StdIntegerType t)
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
                return "int";

            case DataScriptParserTokenTypes.UINT64:
                return "BigInteger";

            default:
                throw new IllegalArgumentException();
        }
    }

    private String getTypeName(BitFieldType t)
    {
        int length = t.getLength();
        if (length == 0)
            return "BigInteger";
        else if (length < 8)
            return "byte";
        else if (length < 16)
            return "short";
        else if (length < 32)
            return "int";
        else if (length < 64)
            return "long";
        else
            return "BigInteger";
    }
    
    private String getTypeName(ArrayType array)
    {
        TypeInterface elType = array.getElementType();
        String elTypeName = getTypeName(elType);
        if (elType instanceof IntegerType)
        {
            if (! elTypeName.equals("BigInteger"))
            {
                return StringUtil.firstToUpper(elTypeName) + "Array";
            }
        }
        return "ObjectArray<" + elTypeName +  ">";        
    }
}
