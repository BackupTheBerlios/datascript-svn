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
package datascript;

import datascript.syntaxtree.Node;
import datascript.syntaxtree.NodeToken;
import java.util.Vector;
import java.util.Iterator;

public class Condition
{
    private Scope scope;
    private String cname;
    private Vector parameters; // Parameter
    private Vector expressions; // Node
    private Node where; // some source location for errors

    Condition(String cname, Vector parameters, Vector expressions, Node where)
    {
        this.cname = cname;
        this.parameters = parameters;
        this.expressions = expressions;
        this.where = where;
    }

    private Condition(String cname, TypeInterface[] pTypes)
    {
        this.cname = cname;
        parameters = new Vector(pTypes.length);
        for (int i = 0; i < pTypes.length; i++)
        {
            parameters.addElement(new Parameter("_" + i, pTypes[i]));
        }
    }

    String getName()
    {
        return cname;
    }

    int getNumberOfParameters()
    {
        return parameters.size();
    }

    Parameter getParameter(int idx)
    {
        return (Parameter) parameters.elementAt(idx);
    }

    Iterator getParameters()
    {
        return parameters.iterator();
    }

    Iterator getExpressions()
    {
        return expressions.iterator();
    }

    NodeToken getSourceLocation()
    {
        return ClosestToken.find(where);
    }

    void setScope(Scope scope)
    {
        this.scope = scope;
    }

    Scope getScope()
    {
        return scope;
    }

    void checkBody(CompoundType ctype)
    {
        where.accept(new ExpressionTypeCheckVisitor(ctype, scope));
    }

    static Condition compareToString = new Condition("compare_to_string",
            new TypeInterface[] { StringType.stringType });

    static Condition builtinConditions[] = new Condition[] { compareToString };

    static Condition isBuiltin(Object obj)
    {
        if (!(obj instanceof String))
        {
            return null;
        }

        for (int i = 0; i < builtinConditions.length; i++)
        {
            if (builtinConditions[i].getName().equals(obj))
            {
                return builtinConditions[i];
            }
        }
        return null;
    }
}
