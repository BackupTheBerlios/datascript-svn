package datascript.emit.java;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.BitFieldType;
import datascript.ast.CompoundType;
import datascript.ast.EnumType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.IntegerType;
import datascript.ast.StructType;
import datascript.ast.TypeInstantiation;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.ast.Value;
import datascript.jet.java.ArrayRead;
import datascript.jet.java.SequenceRead;
import datascript.jet.java.StructBegin;
import datascript.jet.java.StructEnd;

public class StructEmitter
{
    private static String nl = System.getProperties().getProperty("line.separator");
    private StructType struct;
    private JavaEmitter global;
    private SequenceFieldEmitter fieldEmitter;
    private TypeNameEmitter typeNameEmitter;
    private ExpressionEmitter exprEmitter = new ExpressionEmitter();
    private StructBegin beginTmpl = new StructBegin();
    private StructEnd endTmpl = new StructEnd();
    private SequenceRead readTmpl = new SequenceRead();
    private ArrayRead arrayTmpl = new ArrayRead();
    private StringBuilder buffer;
    
    public StructEmitter(JavaEmitter j)
    {
        this.global = j;
        this.fieldEmitter = new SequenceFieldEmitter(j);
        this.typeNameEmitter = new TypeNameEmitter(j);
    }
   
    public StructType getSequenceType()
    {
        return struct;
    }
    
    public JavaEmitter getGlobal()
    {
        return global;
    }
    
    public void begin(StructType s)
    {
        struct = s;
        String result = beginTmpl.generate(this);
        System.out.print(result);
        
        for (Field field : s.getFields())
        {
            //System.out.println("    // field "+ field.getName());
            fieldEmitter.emit(field);
        }
        result = readTmpl.generate(this);
        System.out.print(result);
    }
    
    public void end(StructType s)
    {
        String result = endTmpl.generate(this);
        System.out.print(result);
    }
    
    public void readFields()
    {
        for (Field field : struct.getFields())
        {
            //System.out.println("    // field "+ field.getName());
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
                methodSuffix = "Bits";
                Expression lengthExpr = ((BitFieldType)type).getLengthExpression();
                arg = exprEmitter.emit(lengthExpr);
                break;
            default:
                throw new InternalError("unhandled type = " + type.getType());
        }
        buffer.append(field.getName());
        buffer.append(" = ");
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
        buffer.append("(__in, __cc)");
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
            buffer.append(" = new (");
            buffer.append(elTypeJavaName);
            buffer.append("__in, ");
            buffer.append(getLengthExpression(length));
            buffer.append(");");
        }
    }
    
    private void readEnumField(Field field, EnumType type)
    {
        buffer.append("// enum type");        
    }
    
    private String getLengthExpression(Expression expr)
    {
        // TODO handle variable length
        Value value = expr.getValue();
        int length = (value == null) ? 0 : value.integerValue().intValue();
        
        return Integer.toString(length);
    }
}
