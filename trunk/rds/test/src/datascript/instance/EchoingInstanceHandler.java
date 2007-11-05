package datascript.instance;

import java.math.BigInteger;

import datascript.ast.Field;
import datascript.ast.FieldRegistry;
import datascript.ast.TypeRegistry;

public class EchoingInstanceHandler implements DataScriptInstanceHandler
{
    int indentLevel = 0;
    
    private String indent()
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < indentLevel; i++)
        {
            buffer.append("    ");
        }
        return buffer.toString();
    }
    
    private String getFieldName(int fieldId)
    {
        Field field = FieldRegistry.getField(fieldId);
        String fieldName = field.getName();
        return fieldName;
    }
    
    private void echo(String msg)
    {
        System.out.println(indent() + msg);
    }
    
    @Override
    public void bigIntegerField(int fieldId, BigInteger value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void endArray(int fieldId)
    {
        indentLevel--;
        echo("end array " + getFieldName(fieldId));

    }

    @Override
    public void endArrayElement(int fieldId)
    {
        indentLevel--;
        echo("end array element " + getFieldName(fieldId));

    }

    @Override
    public void endCompound(int fieldId)
    {
        indentLevel--;
        echo("end compound " + getFieldName(fieldId));        
    }

    @Override
    public void endInstance(int typeId)
    {
        indentLevel--;
        String typeName = TypeRegistry.getType(typeId).getName();
        echo("end instance " + typeName);
    }

    @Override
    public void enumField(int fieldId, int value)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void integerField(int fieldId, long value)
    {
        echo("integerField = " + value + ", id = " + fieldId);
    }

    @Override
    public void startArray(int fieldId)
    {
        echo("start array " + getFieldName(fieldId) + " id = " + fieldId);
        indentLevel++;
    }

    @Override
    public void startArrayElement(int fieldId)
    {
        echo("start array element " + getFieldName(fieldId));
        indentLevel++;
    }

    @Override
    public void startCompound(int fieldId)
    {
        echo("start compound " + getFieldName(fieldId));
        indentLevel++;
    }

    @Override
    public void startInstance(int typeId)
    {
        String typeName = TypeRegistry.getType(typeId).getName();
        echo("start instance " + typeName);
        indentLevel++;
    }

    @Override
    public void stringField(int fieldId, String value)
    {
        // TODO Auto-generated method stub

    }

}
