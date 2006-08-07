/*
 * TypeReference.java
 *
 * @author: Godmar Back
 * @version: $Id: TypeReference.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import antlr.Token;
import antlr.collections.AST;
import datascript.tools.ToolContext;

public class TypeReference extends TokenAST implements TypeInterface,
        LinkAction
{
    /** The referenced type. */
    private TypeInterface refType;

    /** Qualified name of this type. */
    private String name;


    public TypeReference()
    {
    }

    public TypeReference(Token token)
    {
        super(token);
    }

    public String getName()
    {
        if (name == null)
        {
            // compute name, only used for printing right now
            StringBuffer b = new StringBuffer();
            for (AST node = getFirstChild(); node != null; node = node
                    .getNextSibling())
            {
                String name = node.getText();
                b.append(name);
                if (node.getNextSibling() != null)
                {
                    b.append(".");
                }
            }
            name = b.toString();
        }
        return name;
    }


    public void link(Context ctxt)
    {
        CompoundType outer = ctxt.getOwner();
        //String outerName = (outer == null) ? "<global>" : outer.getName();
        Object obj = null;
        for (AST node = getFirstChild(); node != null; node = node
                .getNextSibling())
        {
            String name = node.getText();
            obj = ctxt.getSymbol(name);
            //System.out.println("Linking " + name + " in scope " + outerName);
            if (obj == null)
            {
                ToolContext.logError((TokenAST)node, "'" + name + "' is undefined");
            }
            /*
             * TODO: Fix this when we're using StructType etc. instead of
             * CommonAST.
             * 
             * if (!(obj instanceof TypeInterface)) { ToolContext.logError(node,
             * obj + " is not a type"); }
             */
            if (obj instanceof CompoundType)
            {
                CompoundType ctype = (CompoundType) obj;
                ctxt = ctype.getScope();
            }
        }
        
        if (obj instanceof TypeInterface)
        {
            refType = (TypeInterface) obj;
        }
        else
        {
            ToolContext.logError(this, "'" + getName() + "' is not a type name");
        }
        
        if (obj instanceof CompoundType)
        {
            CompoundType inner = (CompoundType) obj;
            if (outer != null && outer.isContainedIn(inner))
            {
                ToolContext.logError(this, "circular containment between '" +
                        inner.getName() + "' and '" + outer.getName() + "'");
            }
            inner.addContainer(outer);
        }
    }

    public void resolve(Context ctxt)
    {

        ctxt.postLinkAction(this);
    }

    public IntegerValue sizeof(Context ctxt)
    {
        return refType.sizeof(ctxt);
    }

    public boolean isMember(Context ctxt, Value val)
    {
        return refType.isMember(ctxt, val);
    }

    public Value castFrom(Value val)
    {
        return refType.castFrom(val);
    }

    public String toString()
    {
        return "TypeReference name='" + name + "' refType='" + refType + "'";
    }

    static public TypeInterface resolveType(TypeInterface type)
    {
        while (type instanceof TypeReference)
        {
            type = ((TypeReference) type).refType;
        }
        return type;
    }

    public Scope getScope()
    {
        throw new InternalError("TypeReference.getScope() not implemented");
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
