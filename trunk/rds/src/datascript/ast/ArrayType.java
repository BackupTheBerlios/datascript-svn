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


import datascript.antlr.util.TokenAST;
import antlr.collections.AST;


@SuppressWarnings("serial")
public class ArrayType extends TokenAST implements TypeInterface
{
    private int id;
    private Scope scope;
    private int length;
    private Expression lengthExpr;
   

    public ArrayType()
    {
        id = TypeRegistry.registerType(this);
    }


    public int getLength()
    {
        Value value = getLengthExpression().getValue();
        length = (value == null) ? 0 : value.integerValue().intValue();
        return length;
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


    public Expression getLengthExpression()
    {
        if (lengthExpr == null)
        {
            lengthExpr = (Expression) getFirstChild().getNextSibling();
        }
        return lengthExpr;
    }


    public boolean isVariable()
    {
        return getFirstChild().getNextSibling() == null;
    }


    public IntegerValue sizeof(Context ctxt)
    {
        IntegerValue eight = new IntegerValue(8);

        IntegerValue size = ((TypeReference) getFirstChild()).bitsizeof(ctxt);
        if (size.remainder(eight).compareTo(new IntegerValue(0)) != 0) 
        {
            throw new RuntimeException("sizeof not integer: " + size);
        }
        return size.multiply(new IntegerValue(getLength())).divide(eight);
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        IntegerValue size = ((TypeReference) getFirstChild()).bitsizeof(ctxt);
        return size.multiply(new IntegerValue(getLength()));
    }


    public boolean isMember(Context ctxt, Value val)
    {
        throw new ComputeError("isMember() not implemented in "
                + this.getClass().getName());
    }


    public Value castFrom(Value val)
    {
        throw new ComputeError("cannot cast " + val + " into " + this);
    }


    public TypeInterface getElementType()
    {
        TypeInterface type = (TypeInterface) getFirstChild();
        return TypeReference.resolveType(type);
    }


    public String toString()
    {
        return "array of " + getElementType();
    }


    public Scope getScope()
    {
        return scope;
    }


    public Package getPackage()
    {
        return getElementType().getPackage();
    }


    public int getId()
    {
        return id;
    }
}
