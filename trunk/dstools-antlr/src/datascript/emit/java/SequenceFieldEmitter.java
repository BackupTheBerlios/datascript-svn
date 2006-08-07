package datascript.emit.java;

import datascript.ast.Field;
import datascript.ast.TypeInterface;
import datascript.ast.TypeReference;
import datascript.jet.java.SequenceFieldAccessor;

public class SequenceFieldEmitter
{
    private JavaEmitter global;
    private Field field;
    
    public SequenceFieldEmitter(JavaEmitter j)
    {
        this.global = j;
    }
   
    public JavaEmitter getGlobal()
    {
        return global;
    }
    
    public void emit(Field f)
    {
        this.field = f;
        SequenceFieldAccessor template = new SequenceFieldAccessor();
        String result = template.generate(this);
        System.out.print(result);
    }
    
    public String getTypeName()
    {
        TypeInterface type = field.getFieldType();
        type = TypeReference.resolveType(type);
        return global.getTypeName(type);
    }
    
    public String getGetterName()
    {
        StringBuffer result = new StringBuffer("get");
        return appendAccessorTail(result);
    }

    public String getSetterName()
    {
        StringBuffer result = new StringBuffer("set");
        return appendAccessorTail(result);
    }

    private String appendAccessorTail(StringBuffer buffer)
    {
        String name = field.getName();
        buffer.append(name.substring(0, 1).toUpperCase());
        buffer.append(name.substring(1, name.length()));
        return buffer.toString();
    }
    
    public String getFieldName()
    {
        return field.getName();
    }
}
