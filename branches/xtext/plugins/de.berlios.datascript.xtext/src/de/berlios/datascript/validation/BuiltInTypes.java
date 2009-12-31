package de.berlios.datascript.validation;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceFactory;

import com.google.inject.Inject;

import de.berlios.datascript.dataScript.DataScriptFactory;
import de.berlios.datascript.dataScript.IntegerType;
import de.berlios.datascript.dataScript.StringType;
import de.berlios.datascript.dataScript.Type;

public class BuiltInTypes
{
    private static IResourceFactory resourceFactory;
    
    private static Resource builtInResource;
    
    private boolean signed;
    private int numBits;
    
    public static IntegerType INTEGER;

    public static IntegerType INT8;
    public static IntegerType INT16;
    public static IntegerType INT32;
    public static IntegerType INT64;

    public static IntegerType UINT8;
    public static IntegerType UINT16;
    public static IntegerType UINT32;
    public static IntegerType UINT64;

    public static IntegerType INT_N;
    public static IntegerType UINT_N;
    
    public static StringType STRING;
    
    public static Type BOOLEAN;
    
    private static Set<IntegerType> builtInTypes = new HashSet<IntegerType>();

    @Inject
    public static void setResourceFactory(IResourceFactory rs)
    {
        resourceFactory = rs;
        builtInResource = resourceFactory.createResource(URI.createURI("dummy:/builtin.ds"));
        assert builtInResource != null;
        
        INT8 = createIntegerType();
        INT16 = createIntegerType();
        INT32 = createIntegerType();
        INT64 = createIntegerType();
        UINT8 = createIntegerType();
        UINT16 = createIntegerType();
        UINT32 = createIntegerType();
        UINT64 = createIntegerType();
        UINT_N = createIntegerType();
        INT_N = createIntegerType();

        INTEGER = INT32;
        
        STRING = createStringType();
        
        BOOLEAN = createBooleanType();
    }
    
    private static IntegerType createIntegerType()
    {
        IntegerType type = DataScriptFactory.eINSTANCE.createIntegerType();
        builtInResource.getContents().add(type);
        builtInTypes.add(type);
        return type;
    }
    
    private static StringType createStringType()
    {
        StringType type = DataScriptFactory.eINSTANCE.createStringType();
        builtInResource.getContents().add(type);
        return type;
    }
    
    private static Type createBooleanType()
    {
        Type type = DataScriptFactory.eINSTANCE.createType();
        builtInResource.getContents().add(type);
        return type;
    }
    
    private BuiltInTypes(int numBits, boolean signed)
    {
        this.numBits = numBits;
        this.signed = signed;
    }
    
    public int getNumBits()
    {
        return numBits;
    }
    
    public boolean isSigned()
    {
        return signed;
    }
    
    public static boolean isInteger(Type type)
    {
        return builtInTypes.contains(type);
    }

    public static boolean isString(Type type)
    {
        return STRING == type;
    }

    public static boolean isBoolean(Type type)
    {
        return BOOLEAN == type;
    }

    /**
     * @param type
     * @return
     */
    public static boolean isBuiltIn(Type type)
    {
        return isInteger(type) || isString(type) || isBoolean(type);
    }
}
