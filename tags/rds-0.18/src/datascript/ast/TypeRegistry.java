package datascript.ast;

import java.util.HashMap;
import java.util.Map;

public class TypeRegistry
{
    private static Map<Integer,TypeInterface> idToTypeMap = new HashMap<Integer, TypeInterface>();
    private static int numTypes = 0;
    
    public static int registerType(TypeInterface type)
    {
        int id = numTypes++;
        idToTypeMap.put(id, type);
        return id;        
    }
    
    public static TypeInterface getType(int id)
    {
        return idToTypeMap.get(id);
    }
    
}
