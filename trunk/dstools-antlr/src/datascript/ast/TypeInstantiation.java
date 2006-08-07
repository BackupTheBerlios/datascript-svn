/*
 * TypeInstantiation.java
 *
 * @author: Godmar Back
 * @version: $Id: TypeInstantiation.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import datascript.tools.ToolContext;


public class TypeInstantiation extends TokenAST implements TypeInterface
{
    /** Reference to a compound type with a parameter list. */
    private CompoundType compound;
    
    public TypeInstantiation()
    {
    }

    public IntegerValue sizeof(Context ctxt)
    {
        throw new ComputeError(this + " sizeof not implemented");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        throw new ComputeError(this + " isMember not implemented");
    }

    public Value castFrom(Value val)
    {
        throw new ComputeError(this + "castFrom not implemented");
    }

    public String toString()
    {
        return "instantiation of " + compound;
    }

    public CompoundType getBaseType()
    {
        return compound;
    }

    public Scope getScope()
    {
        throw new InternalError("TypeReference.getScope() not implemented");
    }
    
    /**
     * Checks if arguments of type instantiation match the formal parameters
     * of the type definition.
     */
    public void checkArguments()
    {
        // get type reference from AST
        TypeReference refType = (TypeReference)getFirstChild();
        
        // lookup referenced type
        TypeInterface p = TypeReference.resolveType(refType);
        
        // this must be a compound type
        if (! (p instanceof CompoundType))
        {
            ToolContext.logError(refType, "'"+ refType.getName() + 
                    "' is not a parameterized type");
        }
        
        // and it must have a parameter list
        compound = (CompoundType) p;
        int numParams = compound.getParameterCount();
        if (numParams == 0)
        {
            ToolContext.logError(refType, "'"+ refType.getName() + 
                    "' is not a parameterized type");
        }
            
        // Number of arguments and parameters must be equal.
        // The type reference is also a child of this node, this accounts for
        // the -1.
        if (getNumberOfChildren()-1 != numParams)
        {
            ToolContext.logError(refType, "wrong number of parameters");
            
        }
        
        // Get scope of parameterized compound type to look up names of
        // formal parameters.
        Scope scope = compound.getScope();
        
        // Iterate over arguments
        Expression arg = (Expression)refType.getNextSibling();
        for (int paramIndex = 0; paramIndex < numParams; 
             paramIndex++, arg = (Expression)arg.getNextSibling())
        {
            // Get parameter name corresponding to current argument
            String paramName = compound.getParameterAt(paramIndex);
            // Lookup the type in scope. This will be a defined type or a
            // built-in type, so we need to resolve it.
            TypeInterface paramType = (TypeInterface)scope.getSymbol(paramName);
            paramType = TypeReference.resolveType(paramType);
            
            // Types must be compatible.
            if (!IntegerType.checkCompatibility(paramType, arg.getExprType()))
            {
                ToolContext.logError(arg, "type mismatch in argument " + 
                        (paramIndex+1));
            }                
        }
    }

    public int getLength()
    {
        throw new InternalError("not implemented");
    }
    
    public Expression getLengthExpression()
    {
        throw new InternalError("not implemented");
    }
}
