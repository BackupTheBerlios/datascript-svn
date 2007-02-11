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
        //System.out.println("to be resolved: " + getName() + " in scope " + ctxt.getOwner().getName());
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
        while (true)
        {
            if (type instanceof TypeReference)
                type = ((TypeReference) type).refType;
            else if (type instanceof Subtype)
                type = ((Subtype)type).getBaseType();
            else
                break;
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
