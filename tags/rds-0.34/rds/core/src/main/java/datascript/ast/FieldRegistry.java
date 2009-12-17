package datascript.ast;

import java.util.HashMap;
import java.util.Map;

public class FieldRegistry
{
    private static Map<Integer, Field> idToFieldMap = new HashMap<Integer, Field>();
    private static int numFields;
    
    public static int registerField(Field field)
    {
        int id = numFields++;
        idToFieldMap.put(id, field);
        return id;        
    }
    
    public static Field getField(int id)
    {
        return idToFieldMap.get(id);
    }
}
