/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
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
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;



@SuppressWarnings("serial")
public class ConstType extends TokenAST implements TypeInterface
{
    private int id;
    private String name;
    private Package pkg;
    private Scope scope;


    public ConstType()
    {
        id = TypeRegistry.registerType(this);
    }


    public ConstType(Token token)
    {
        super(token);
    }


    public String getName()
    {
        if (name == null)
        {
            AST node = getFirstChild().getNextSibling();
            name = node.getText();
        }
        return name;
    }


    public TypeInterface getBaseType()
    {
        return (TypeInterface) getFirstChild();
    }


    public IntegerValue sizeof(Context ctxt)
    {
        return getBaseType().sizeof(ctxt);
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        return getBaseType().bitsizeof(ctxt);
    }


    public boolean isMember(Context ctxt, Value val)
    {
        return getBaseType().isMember(ctxt, val);
    }


    public Value castFrom(Value val)
    {
        return getBaseType().castFrom(val);
    }


    public Value getValue()
    {
        return ((IntegerExpression) getFirstChild().getNextSibling()
                .getNextSibling()).getValue();
    }


    public int getLength()
    {
        throw new InternalError("getLength() not implemented in "
                + this.getClass().getName());
    }


    public Expression getLengthExpression()
    {
        throw new InternalError("getLengthExpression() not implemented in "
                + this.getClass().getName());
    }


    public String getDocumentation()
    {
        String result = "";
        AST n = findFirstChildOfType(DataScriptParserTokenTypes.DOC);
        if (n != null)
        {
            result = n.getText();
        }
        return result;
    }


    public String toString()
    {
        return "CONSTTYPE";
    }


    public Package getPackage()
    {
        return pkg;
    }


    public void setPackage(Package pkg)
    {
        this.pkg = pkg;
    }


    public Scope getScope()
    {
        return scope;
    }


    public void setScope(Scope scope, Package pkg)
    {
        this.scope = scope;
        this.pkg = pkg;
        scope.setOwner(this);
    }


    @Override
    public int getId()
    {
        return id;
    }

}
