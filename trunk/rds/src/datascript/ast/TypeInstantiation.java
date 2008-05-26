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


import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;



@SuppressWarnings("serial")
public class TypeInstantiation extends TokenAST implements TypeInterface
{
    private int id;
    
    /** Reference to a compound type with a parameter list. */
    private CompoundType compound;

    private List<Expression> arguments;


    public TypeInstantiation()
    {
        id = TypeRegistry.registerType(this);
    }


    public IntegerValue sizeof(Scope ctxt)
    {
        throw new ComputeError(this + " sizeof not implemented");
    }


    public IntegerValue bitsizeof(Scope ctxt)
    {
        throw new ComputeError(this + " bitsizeof not implemented");
    }


    public boolean isMember(Scope ctxt, Value val)
    {
        throw new ComputeError(this + " isMember not implemented");
    }


    public Value castFrom(Value val)
    {
        throw new ComputeError(this + "castFrom not implemented");
    }


    @Override
    public String toString()
    {
        return "instantiation of " + compound;
    }


    public String getName()
    {
        AST node = getFirstChild();
        if (node != null)
        {
            return node.getText();
        }
        return null;
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
        TypeReference refType = (TypeReference) getFirstChild();

        // lookup referenced type
        TypeInterface p = TypeReference.getBaseType(refType);

        // this must be a compound type
        if (!(p instanceof CompoundType))
        {
            ToolContext.logError(refType, "'" + refType.getName()
                    + "' is not a parameterized type");
        }

        // and it must have a parameter list
        compound = (CompoundType) p;
        if (compound == null)
        {
            ToolContext.logError(refType, "'" + refType.getName()
                    + "' has no parameterized types");
            return;
        }
        int numParams = compound.getParameterCount();
        if (numParams == 0)
        {
            ToolContext.logError(refType, "'" + refType.getName()
                    + "' is not a parameterized type");
        }

        // Number of arguments and parameters must be equal.
        // The type reference is also a child of this node, this accounts for
        // the -1.
        if (getNumberOfChildren() - 1 != numParams)
        {
            ToolContext.logError(refType, "wrong number of parameters");
        }

        // Iterate over arguments
        arguments = new ArrayList<Expression>(numParams);
        AST arg = refType.getNextSibling();
        for (int paramIndex = 0; 
             paramIndex < numParams; 
             paramIndex++, arg = arg.getNextSibling())
        {
            Expression expr = (Expression) arg;
            arguments.add(expr);
            if (arg.getType() != DataScriptParserTokenTypes.EXPLICIT)
            {
                // Get parameter corresponding to current argument
                Parameter param = compound.getParameterAt(paramIndex);
                // Resolve the type.
                TypeInterface paramType = param.getType();
                paramType = TypeReference.getBaseType(paramType);

                // Types must be compatible.
                if (!Expression.checkCompatibility(paramType, expr
                        .getExprType()))
                {
                    ToolContext.logError(expr, "type mismatch in argument "
                            + (paramIndex + 1));
                }
            }
        }
    }


    public List<Expression> getArguments()
    {
        return arguments;
    }


    public int getLength()
    {
        throw new InternalError("not implemented");
    }


    public Expression getLengthExpression()
    {
        throw new InternalError("not implemented");
    }


    public Package getPackage()
    {
        return getBaseType().getPackage();
    }


    public int getId()
    {
        return id;
    }

}
