package datascript.instance;

import java.io.IOException;

import datascript.antlr.DataScriptParserTokenTypes;
import datascript.ast.ArrayType;
import datascript.ast.CompoundType;
import datascript.ast.Expression;
import datascript.ast.Field;
import datascript.ast.Package;
import datascript.ast.StdIntegerType;
import datascript.ast.TypeInterface;
import datascript.runtime.io.BitStreamReader;
import datascript.runtime.io.ByteArrayBitStreamReader;

public class AstDataScriptInstanceParser implements DataScriptInstanceParser
{
    private BitStreamReader reader;
    private DataScriptInstanceHandler handler;
    private ExpressionEvaluator ee = new ExpressionEvaluator();
    
    public AstDataScriptInstanceParser(BitStreamReader reader)
    {
        this.reader = reader;
    }
    
    public AstDataScriptInstanceParser(byte[] byteArray) throws IOException
    {        
        this.reader = new ByteArrayBitStreamReader(byteArray);
    }
    
    @Override
    public void parse(String typeName) throws IOException
    {
        Package rootPackage = Package.getRoot();
        TypeInterface type = rootPackage.getType(typeName);
        if (type instanceof CompoundType)
        {
            int typeId = getId(type);
            handler.startInstance(typeId);
            parse(type);
            handler.endInstance(typeId);
        }
    }
    
    public void parse(TypeInterface type) throws IOException
    {
        CompoundType compound = (CompoundType) type;
        parseCompound(compound);
    }
    
    private void parseCompound(CompoundType compound) throws IOException
    {
        for (Field field : compound.getFields())
        {
            parseField(field);
        }
    }

    private void parseField(Field field) throws IOException
    {
        int fieldId = field.getId();
        TypeInterface type = field.getFieldType();
        if (type instanceof CompoundType)
        {
            CompoundType compound = (CompoundType) type;
            handler.startCompound(fieldId);
            parseCompound(compound);
            handler.endCompound(fieldId);
        }
        else if (type instanceof ArrayType)
        {
            ArrayType array = (ArrayType) type;
            handler.startArray(fieldId);
            Expression length = array.getLengthExpression();
            long numElems = ee.evaluate(length);
            TypeInterface elementType = array.getElementType();
            for (int i = 0; i < numElems; i++)
            {
                handler.startArrayElement(fieldId);
                parse(elementType);
                handler.endArrayElement(fieldId);                
            }
            handler.endArray(fieldId);
        }
        else if (type instanceof StdIntegerType)
        {
            StdIntegerType intType = (StdIntegerType) type;
            parseStdIntegerType(field, intType);
        }
        
    }

    private void parseStdIntegerType(Field field, StdIntegerType intType) throws IOException 
    {
        long value;
        switch (intType.getType())
        {
            case DataScriptParserTokenTypes.INT8:
                value = reader.readByte();
                break;
            case DataScriptParserTokenTypes.INT16:
                value = reader.readShort();
                break;
            case DataScriptParserTokenTypes.INT32:
                value = reader.readInt();
                break;
            case DataScriptParserTokenTypes.INT64:
                value = reader.readLong();
                break;
            case DataScriptParserTokenTypes.UINT8:
                value = reader.readUnsignedByte();
                break;
            case DataScriptParserTokenTypes.UINT16:
                value = reader.readUnsignedShort();
                break;
            case DataScriptParserTokenTypes.UINT32:
                value = reader.readUnsignedInt();
                break;
            case DataScriptParserTokenTypes.UINT64:
                throw new UnsupportedOperationException();
                
            default:
                throw new IllegalStateException();
        }
        if (field.isUsedInExpression())
        {
            ee.storeFieldValue(field.getId(), value);
        }
        handler.integerField(field.getId(), value);
    }

    @Override
    public void setInstanceHandler(DataScriptInstanceHandler handler)
    {
        this.handler = handler;
    }
    
    private int getId(TypeInterface type)
    {
        return 0;
    }

}
