/* BSD License
 *
 * Copyright (c) 2006, Harald Wellmann, Harman/Becker Automotive Systems
 * All rights reserved.
 * 
 * This software is derived from previous work
 * Copyright (c) 2003, Godmar Back.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 * 
 *     * Neither the name of Harman/Becker Automotive Systems or
 *       Godmar Back nor the names of their contributors may be used to
 *       endorse or promote products derived from this software without
 *       specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package datascript.ast;


import antlr.Token;
import antlr.collections.AST;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;



/**
 * Any occurrence of a type name which is not the defining one is a type
 * reference and has an AST node of this class.
 * <p>
 * In the link phase, the type reference is mapped to the corresponding type.
 * <p>
 * A type reference is either a simple name or a sequence of simple names
 * separated by dots referring to a nested type, e.g. {@code Outer.Inner}.
 * 
 * @author HWellmann
 * 
 */
@SuppressWarnings("serial")
public class TypeReference extends TokenAST implements TypeInterface,
        LinkAction
{
    /** The referenced type. */
    private TypeInterface refType;

    /**
     * Qualified name of referenced type.
     * 
     * @see #getName().
     */
    private String name;

    private boolean hasArguments = false;
    
    private boolean ignoreArguments = false;


    public boolean getIgnoreArguments()
    {
        return ignoreArguments;
    }


    public void setIgnoreArguments(boolean ignoreArguments)
    {
        this.ignoreArguments = ignoreArguments;
    }


    public TypeReference()
    {

    }


    /** Constructs a type reference from a token. */
    public TypeReference(Token token)
    {
        super(token);
    }


    /**
     * Returns the qualified name of this type within the current package, e.g.
     * {@code Outer.Inner}. This is different from the fully qualified name
     * which includes the package, e.g. {@code com.acme.foo.bar.Outer.Inner}.
     * 
     * @return type name
     */
    public String getName()
    {
        if (name == null)
        {
            // compute name, only used for printing right now
            StringBuffer b = new StringBuffer();
            AST node = getFirstChild();
            for (; node != null; node = node.getNextSibling())
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


    /**
     * Links this reference to the corresponding type and logs an error if there
     * is a cycle in the containment hierarchy.
     */
    public void link(Context ctxt)
    {
        CompoundType outer = (CompoundType) ctxt.getOwner();

        // From left to right, link each part of a nested reference.
        AST node = getFirstChild();
        for (; node != null; node = node.getNextSibling())
        {
            String name = node.getText();
            refType = ctxt.getType(name);
            // System.out.println("Linking " + name + " in scope " + outerName);
            if (refType == null)
            {
                ToolContext.logError((TokenAST) node, "'" + name
                        + "' is undefined");
            }
            if (refType instanceof CompoundType)
            {
                CompoundType ctype = (CompoundType) refType;
                ctxt = ctype.getScope();
            }
        }

        if (refType instanceof CompoundType)
        {
            CompoundType inner = (CompoundType) refType;
            // Check for missing arguments
            if (inner.getParameterCount() > 0 && !hasArguments && !ignoreArguments)
            {
                ToolContext.logError((TokenAST) getFirstChild(), inner.getName()
                        + " is defined as parameterized type");
            }
            else if (inner.getParameterCount() <= 0 && hasArguments)
            {
                ToolContext.logError((TokenAST) getFirstChild(), inner.getName()
                        + " is defined without an argument list");
            }
            
            if (outer == null)
                return;

            // Check for circular containment
            if (outer.isContainedIn(inner))
            {
                ToolContext.logError(this, "circular containment between '"
                        + inner.getName() + "' and '" + outer.getName() + "'");
            }
            inner.addContainer(outer);
        }
    }


    public IntegerValue sizeof(Context ctxt)
    {
        return refType.sizeof(ctxt);
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        return refType.bitsizeof(ctxt);
    }


    public boolean isMember(Context ctxt, Value val)
    {
        return refType.isMember(ctxt, val);
    }


    public Value castFrom(Value val)
    {
        return refType.castFrom(val);
    }


    @Override
    public String toString()
    {
        return "TypeReference name='" + name + "' refType='" + refType + "'";
    }


    static public TypeInterface getBaseType(TypeInterface type)
    {
        while (true)
        {
            if (type instanceof TypeReference)
                type = ((TypeReference) type).refType;
            else if (type instanceof Subtype)
                type = ((Subtype) type).getBaseType();
            else 
                break;
        }
        return type;
    }


    static public TypeInterface resolveType(TypeInterface type)
    {
        while (true)
        {
            if (type instanceof TypeReference)
                type = ((TypeReference) type).refType;
            else 
                break;
        }
        return type;
    }


    public Scope getScope()
    {
        throw new UnsupportedOperationException();
    }


    public int getLength()
    {
        throw new UnsupportedOperationException();
    }


    public Expression getLengthExpression()
    {
        throw new UnsupportedOperationException();
    }


    public Package getPackage()
    {
        return refType.getPackage();
    }


    public void setArgumentsPresent(boolean hasArguments)
    {
        this.hasArguments = hasArguments;
    }


    public int getId()
    {
        return refType.getId();
    }
}
