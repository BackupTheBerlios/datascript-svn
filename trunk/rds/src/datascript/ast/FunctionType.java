/* BSD License
 *
 * Copyright (c) 2007, Harald Wellmann, Harman/Becker Automotive Systems
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



/**
 * This class represents a function within a compound type.
 * Functions are currently restricted to have empty parameter lists and 
 * integer return types.
 */
@SuppressWarnings("serial")
public class FunctionType extends TokenAST implements TypeInterface
{
    private int id;
    private String name;
    private CompoundType owner;
    private TypeInterface returnType;
    private Expression result;


    public FunctionType()
    {
        id = TypeRegistry.registerType(this);
    }


    public FunctionType(Token token)
    {
        super(token);
    }


    private void initMembersIfNeeded()
    {
        if (name == null)
        {
            AST ast = getFirstChild();
            name = ast.getText();
            ast = ast.getNextSibling();
            returnType = (TypeInterface) ast;
            returnType = TypeReference.resolveType(returnType);
            result = (Expression) ast.getNextSibling().getFirstChild();
        }
    }


    public String getName()
    {
        initMembersIfNeeded();
        return name;
    }


    public Expression getResult()
    {
        initMembersIfNeeded();
        return result;
    }


    public TypeInterface getReturnType()
    {
        initMembersIfNeeded();
        return returnType;
    }


    public void setOwner(AST ast)
    {
        owner = (CompoundType) ast;
    }


    public CompoundType getOwner()
    {
        return owner;
    }


    public int getLength()
    {
        throw new UnsupportedOperationException();
    }


    public Value castFrom(Value val)
    {
        throw new UnsupportedOperationException();
    }


    public Scope getScope()
    {
        return null;
    }


    public boolean isMember(Context ctxt, Value val)
    {
        throw new UnsupportedOperationException();
    }


    public Package getPackage()
    {
        return owner.getPackage();
    }


    public IntegerValue sizeof(Context ctxt)
    {
        throw new UnsupportedOperationException();
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        throw new UnsupportedOperationException();
    }


    public Expression getLengthExpression()
    {
        return null;
    }


    @Override
    public int getId()
    {
        return id;
    }
}
