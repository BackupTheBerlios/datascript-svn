package de.berlios.datascript.validation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceFactory;

import com.google.inject.Inject;

import de.berlios.datascript.dataScript.ArrayType;
import de.berlios.datascript.dataScript.DataScriptFactory;
import de.berlios.datascript.dataScript.Field;
import de.berlios.datascript.dataScript.FunctionType;
import de.berlios.datascript.dataScript.Type;

public class ImplicitTypes
{
    private static IResourceFactory resourceFactory;
    
    private static Resource implicitResource;
    
    @Inject
    public static void setResourceFactory(IResourceFactory rs)
    {
        resourceFactory = rs;
        implicitResource = resourceFactory.createResource(URI.createURI("dummy:/implicit.ds"));
        assert implicitResource != null;
        
    }
    
    public static FunctionType createFunctionType(Type resultType)
    {
        FunctionType type = DataScriptFactory.eINSTANCE.createFunctionType();
        type.setResult(resultType);
        implicitResource.getContents().add(type);
        return type;
    }

    public static ArrayType createArrayType(Type elementType, Field field)
    {
        ArrayType type = DataScriptFactory.eINSTANCE.createArrayType();
        type.setElementType(elementType);
        type.setField(field);
        implicitResource.getContents().add(type);
        return type;
    }
}
