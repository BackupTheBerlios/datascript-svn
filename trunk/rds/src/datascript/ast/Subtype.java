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
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;



@SuppressWarnings("serial")
public class Subtype extends TokenAST implements TypeInterface
{
    private int id;
    private String name;
    private Package pkg;


    public Subtype()
    {
        id = TypeRegistry.registerType(this);
    }


    public Subtype(Token token)
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


    public IntegerValue sizeof(Context ctx)
    {
        return getBaseType().sizeof(ctx);
    }


    public IntegerValue bitsizeof(Context ctx)
    {
        return getBaseType().bitsizeof(ctx);
    }


    public Scope getScope()
    {
        throw new UnsupportedOperationException(
                "TypeReference.getScope() not implemented");
    }


    public int getLength()
    {
        throw new UnsupportedOperationException("not implemented");
    }


    public Expression getLengthExpression()
    {
        throw new UnsupportedOperationException("not implemented");
    }


    public boolean isMember(Context ctxt, Value val)
    {
        return getBaseType().isMember(ctxt, val);
    }


    public Value castFrom(Value val)
    {
        return getBaseType().castFrom(val);
    }


    public String getDocumentation()
    {
        String result = "";
        Token t = getHiddenBefore();
        if (t != null && t.getType() == DataScriptParserTokenTypes.DOC)
        {
            result = t.getText();
        }
        return result;
    }


    public String toString()
    {
        return "SUBTYPE";
    }


    public Package getPackage()
    {
        return pkg;
    }


    public void setPackage(Package pkg)
    {
        this.pkg = pkg;
    }


    public int getId()
    {
        return id;
    }
}
