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

import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import datascript.syntaxtree.Node;
import datascript.syntaxtree.NodeToken;

public class Constraint extends JavaExpressionEmitter
{
    String expr;
    boolean assertType;
    Stack stack = new Stack();
    String base;
    HashMap symbols;
    String fieldAccessChar;

    Constraint(CompoundType type, Field f, Node n, String base, HashMap s)
    {
        this(null, type, base, s);
        ExpressionTypeCheckVisitor jvis = new ExpressionTypeCheckVisitor(f
                .getCompound(), type.getScope(), this);
        n.accept(jvis);
    }

    Constraint(String str, CompoundType type, String base, HashMap s)
    {
        super(type);
        this.base = base;
        this.expr = str;
        this.assertType = true;
        this.symbols = s;
        this.fieldAccessChar = ConstraintsList.fieldAccessChar;
    }

    public void setCvcType(boolean assertType)
    {
        this.assertType = assertType;
    }

    public boolean getCvcType()
    {
        return assertType;
    }

    public void update(String str)
    {
        expr = str;
    }

    public String toString()
    {
        return expr;
    }

    public void push()
    {
        stack.push(expr);
        super.push();
    }

    public String pop()
    {
        super.pop();
        return (String) stack.pop();
    }

    public String top()
    {
        return (String) stack.peek();
    }

    public void addSymbol(String s, Field field)
    {
        if (!symbols.containsKey(s))
            symbols.put(s, field);
    }

    public void qualifiedFieldAccess(Field field, String base)
    {
        currentExpr = base + field.getName();
    }

    public void qualifiedFieldAccess(Field field)
    {
        qualifiedFieldAccess(field, currentExpr + fieldAccessChar);
        expr = getAbsoluteName(currentExpr);
        if (field.getType() instanceof BuiltinType)
        {
            addSymbol(expr, field);
        }
    }

    public String getAbsoluteName(String str)
    {
        Scope scope = getType().getScope();
        String res = new String(str);
        String newBase = new String(base);

        String first = (new StringTokenizer(res, fieldAccessChar)).nextToken();

        while (scope.getSymbolFromThis(first) == null)
        {
            newBase = newBase
                    .substring(0, newBase.lastIndexOf(fieldAccessChar));
            scope = (Scope) scope.getParentScope();
        }

        return newBase + fieldAccessChar + str;
    }

    public void fieldAccess(Field field)
    {
        super.fieldAccess(field);
        expr = base + fieldAccessChar + field.getName();
        if (field.getType() instanceof BuiltinType)
        {
            addSymbol(expr, field);
        }
    }

    public void compoundReference(CompoundType ctype)
    {
        super.compoundReference(ctype);
    }

    public void unaryOperation(NodeToken n)
    {
        expr = n.tokenImage + expr;
        super.unaryOperation(n);
    }

    public void binaryOperation(NodeToken n)
    {
        expr = top() + getOperator(n.tokenImage) + expr;
        super.binaryOperation(n);
    }

    public String getOperator(String op)
    {
        if (op.equals("=="))
            return " = ";
        if (op.equals("&&"))
            return " AND ";
        if (op.equals("||"))
            return " OR ";
        return op;
    }

    public void value(Value value)
    {
        super.value(value);
        expr = result();
        if (value instanceof IntegerValue)
        {
            expr = value.integerValue().intValue() + "";
        }
    }

    public void print(String s)
    {
        System.out.println(s);
    }
}
