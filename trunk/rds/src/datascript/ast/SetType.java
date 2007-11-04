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


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import antlr.Token;
import antlr.collections.AST;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.TokenAST;



@SuppressWarnings("serial")
public class SetType extends TokenAST implements TypeInterface
{

    protected final HashMap<Object, Value> items = new HashMap<Object, Value>();

    protected int id;
    protected TypeInterface type;
    protected Package pkg;

    protected String name;

    CompoundType ctype; // compound in which this set type is defined


    public SetType()
    {
        id = TypeRegistry.registerType(this);
    }


    public String getName()
    {
        if (name == null)
        {
            AST n = getFirstChild().getNextSibling();
            name = n.getText();
        }
        return name;
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


    public TypeInterface getBaseType()
    {
        return (TypeInterface) getFirstChild();
    }


    Value[] getValues()
    {
        Iterator<Value> it = items.values().iterator();
        Value[] values = new Value[items.size()];
        for (int i = 0; i < values.length; i++)
        {
            values[i] = it.next();
        }
        Arrays.sort(values);
        return values;
    }


    /**
     * TODO: not clear what this means - finding out whether it's a valid
     * combination of non-disjoint bitmask entries takes some work
     */
    public boolean isMember(Context ctxt, Value val)
    {
        Iterator<Object> it = items.keySet().iterator();
        while (it.hasNext())
        {
            if (val.compareTo(it.next()) == 0)
            {
                return true;
            }
        }
        return false;
    }


    public IntegerValue sizeof(Context ctxt)
    {
        return getBaseType().sizeof(ctxt);
    }


    public IntegerValue bitsizeof(Context ctxt)
    {
        return getBaseType().bitsizeof(ctxt);
    }


    public Value castFrom(Value val)
    {
        throw new InternalError("SetType.castFrom() not implemented");
    }


    public Scope getScope()
    {
        return null;
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
        return pkg;
    }


    @Override
    public int getId()
    {
        return id;
    }

}
