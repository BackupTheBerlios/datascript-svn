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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import datascript.syntaxtree.Node;

public class ConstraintsList
{
    PrintWriter out;
    CompoundType type;
    String base;
    Constraint cnst;
    ConstraintsList next;
    ConstraintsList unionList;
    static HashMap symbols = new HashMap();
    static String fieldAccessChar = "_";

    ConstraintsList(PrintWriter out, CompoundType type)
    {
        this(out, type, "", null, null, null);
        for (Iterator i = type.getNestedTypes(); i.hasNext();)
        {
            CompoundType ntype = (CompoundType) i.next();
            ConstraintsList l = makeList(out, ntype, ntype.getName());
            l.buildList();
            add(l);
        }
    }

    ConstraintsList(PrintWriter out, CompoundType type, String base,
            Constraint c, ConstraintsList n, ConstraintsList unionList)
    {
        this.out = out;
        this.type = type;
        this.base = base;
        this.cnst = c;
        this.next = n;
        this.unionList = unionList;
    }

    ConstraintsList(PrintWriter out, CompoundType type, String base)
    {
        this(out, type, base, null, null, null);
    }

    ConstraintsList makeList(PrintWriter out, CompoundType type, String base,
            Constraint c, ConstraintsList n, ConstraintsList un)
    {
        return new ConstraintsList(out, type, base, c, n, un);
    }

    ConstraintsList makeList(PrintWriter out, CompoundType type, String base)
    {
        return makeList(out, type, base, null, null, null);
    }

    Constraint makeConstraint(CompoundType type, Field f, Node n, String base)
    {
        return new Constraint(type, f, n, base, symbols);
    }

    Constraint makeConstraint(String expr, CompoundType type, String base)
    {
        return new Constraint(expr, type, base, symbols);
    }

    public void buildList()
    {
        ConstraintsList list = makeList(out, type, base);
        if (type instanceof StructType)
        {
            list.buildStructList();
            add(list);
        }
        else if (type instanceof UnionType)
        {
            list.buildUnionList();
            add(list);
        }
    }

    public void buildStructList()
    {
        for (Iterator i = type.getFields(); i.hasNext();)
        {
            buildFieldList((Field) i.next());
        }
    }

    public void buildFieldList(Field f)
    {
        Constraint c;
        TypeInterface t = f.getType();
        String newBase = base + fieldAccessChar + f.getName();

        if (f.getInitializer() != null)
        {
            c = makeConstraint(type, f, f.getInitializer(), base);
            addSymbol(newBase, f);
            c.update(newBase + " = " + c.toString());
            add(c);
        }
        if (f.getCondition() != null)
        {
            c = makeConstraint(type, f, f.getCondition(), base);
            add(c);
        }
        if (t instanceof CompoundType)
        {
            // Struct type
            ConstraintsList l = makeList(out, (CompoundType) t, newBase);
            l.buildList();
            add(l);
        }
    }

    public void relateUnion(Field a, String basea, Field b, String baseb)
    {
        if (a.getType() instanceof BuiltinType)
        {
            if (b.getType() instanceof BuiltinType)
            {
                String expr = basea + fieldAccessChar + a.getName() + " = "
                        + baseb + fieldAccessChar + b.getName();
                Constraint c = makeConstraint(expr, a.getCompound(), basea);
                add(c);
            }
        }
    }

    public void relateUnion(Field a, Field b)
    {
        relateUnion(a, base, b, base);
    }

    public void buildUnionList()
    {
        Field first = null;

        for (Iterator i = type.getFields(); i.hasNext();)
        {
            Field f = (Field) i.next();
            ConstraintsList list = makeList(out, type, base);
            list.buildFieldList(f);
            addUnion(list);
            if (first == null)
                first = f;
            else
                relateUnion(f, first);
        }
    }

    public void add(ConstraintsList l)
    {
        ConstraintsList t = this;

        while (t.next != null)
            t = t.next;
        t.next = l;
    }

    public void addUnion(ConstraintsList l)
    {
        ConstraintsList newList = makeList(out, type, base);
        newList.next = l;
        newList.unionList = unionList;
        unionList = newList;

        ConstraintsList t = unionList;
        String expr = "";

        expr = "" + unionList;
        while (t.unionList != null)
        {
            // do Union
            expr = expr + " AND NOT (" + t.unionList + ")";
            t = t.unionList;
        }
        if (cnst == null)
            add(makeConstraint(expr, type, base));
        else
            cnst.update(cnst + " OR (" + expr + ")");
        Constraint c = makeConstraint(expr, type, base);
        c.setCvcType(false);
        add(c);
    }

    public void add(Constraint c)
    {
        if (cnst == null)
        {
            cnst = c;
        }
        else
        {
            next = makeList(out, type, base, c, next, null);
        }
    }

    public void addSymbol(String s, Field field)
    {
        // Note: Constraint.addSymbol is similar to this
        if (!symbols.containsKey(s))
            symbols.put(s, field);
    }

    public void printSymbols()
    {
        Set s = symbols.keySet();
        Iterator i = s.iterator();
        if (i.hasNext())
        {
            out.println((String) i.next());
            while (i.hasNext())
            {
                out.println(", " + (String) i.next());
            }
            out.println(": REAL;");
        }
        symbols.clear();
    }

    public void print()
    {
        if (cnst != null)
            out.println(cnst.toString());
        if (next != null)
            next.print();
    }

    public String toString()
    {
        String t = "";
        if (cnst != null && next == null)
            t = t + "(" + cnst + ")";
        if (cnst != null && next != null)
            t = "(" + cnst + ") AND " + next;
        if (cnst == null && next != null)
            t = t + next;
        return t;
    }
}
