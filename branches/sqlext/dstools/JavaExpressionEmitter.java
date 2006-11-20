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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import datascript.parser.DSConstants;
import datascript.syntaxtree.Node;
import datascript.syntaxtree.NodeToken;

public class JavaExpressionEmitter implements ExpressionEmitter
{
    // compound type relative to which this expression is output
    private CompoundType ctype;

    // context used to resolve names for this expression
    // this is most often the compound type's scope, but can be a
    // subscope as in conditions
    private Context ctxt;
    private Stack emitStack = new Stack();
    protected String currentExpr;

    JavaExpressionEmitter(CompoundType ctype, Context ctxt)
    {
        this.ctype = ctype;
        this.ctxt = ctxt;
    }

    JavaExpressionEmitter(CompoundType ctype)
    {
        this(ctype, ctype.getScope());
    }

    public CompoundType getType()
    {
        return ctype;
    }

    public Context getContext()
    {
        return ctxt;
    }

    public String result()
    {
        return currentExpr;
    }

    public void push()
    {
        // System.out.println("pushing `" + currentExpr + "'");
        emitStack.push(currentExpr);
    }

    public String pop()
    {
        return (String) emitStack.pop();
    }

    public String top()
    {
        return (String) emitStack.peek();
    }

    public void arrayAccess(ArrayType atype, TypeInterface etype)
    {
        String lowerBoundAdjustment = "";
        if (atype.lowerBound != null)
        {
            lowerBoundAdjustment = JavaEmitter
                    .getLowerBound(atype, ctxt, ctype);
            lowerBoundAdjustment = " - (" + lowerBoundAdjustment + ")";
        }
        currentExpr = "("
                + ((etype instanceof CompoundType) ? ("("
                        + JavaEmitter.printType(etype) + ")") : "") + pop()
                + ".elementAt(/*XXX*/(int)(" + currentExpr
                + lowerBoundAdjustment + ")))";
    }

    public void qualifiedFieldAccess(Field field, String base)
    {
        TypeInterface ftype = field.getType();
        // we must switch to _choice if this field is an arm in a union */
        if (field.getCompound() instanceof UnionType)
        {
            currentExpr = base + "get"
                    + JavaEmitter.makeAccessor(field.getName()) + "()";
        }
        else
        {
            currentExpr = base + field.getName();
        }

        // suppress sign-expansion for unsigned types less than 64bit
        if (field.getType() instanceof BuiltinType)
        {
            switch (((BuiltinType) field.getType()).kind)
            {
                case DSConstants.UINT8:
                    currentExpr = "((" + currentExpr + ") & 0xff)";
                    break;
                case DSConstants.UINT16:
                case DSConstants.LEUINT16:
                    currentExpr = "((" + currentExpr + ") & 0xffff)";
                    break;
                case DSConstants.UINT32:
                case DSConstants.LEUINT32:
                    currentExpr = "((" + currentExpr + ") & 0xffffffffL)";
                    break;
            }
        }
    }

    public void qualifiedFieldAccess(Field field)
    {
        qualifiedFieldAccess(field, currentExpr + ".");
    }

    public void fieldAccess(Field field)
    {
        qualifiedFieldAccess(field, "");
    }

    public void sizeof()
    {
        currentExpr = currentExpr + ".sizeof()";
    }

    public void lengthof()
    {
        currentExpr = currentExpr + ".length()";
    }

    public void value(Value value)
    {
        if (value instanceof StringValue)
        {
            currentExpr = "\"" + value.stringValue() + "\"";
        }
        else
        {
            // XXX use "new BigInteger("....")" for BigInts
            currentExpr = value.toString();
        }
    }

    public void compoundReference(CompoundType ctype)
    {
        String tname = JavaEmitter.printType(ctype);
        currentExpr = "((" + tname + ")" + "__cc.find(\"" + tname + "\"))";
    }

    public void openConditionCall(Condition cond)
    {
        currentExpr = cond.getName();
    }

    public void openQualifiedConditionCall(Condition cond)
    {
        currentExpr = "(" + currentExpr + ")." + cond.getName();
    }

    /*
     * On stack arg_n ... arg_0 condition
     */
    public void closeConditionCall(Condition cond, int nargs)
    {
        String s = pop() + ")";
        while (--nargs > 0)
        {
            s = pop() + ", " + s;
        }
        currentExpr = pop() + "(" + s;
    }

    public void localParameter(Parameter param)
    {
        currentExpr = param.getName();
    }

    public void choiceCheck(UnionType utype, Field choice)
    {
        // currentExpr = " (" + currentExpr + "._choice == " +
        // JavaEmitter.fullyQualifiedTypeName(utype) + ".CHOICE_" +
        // choice.getName() + ") ";
        currentExpr = " (((" + JavaEmitter.fullyQualifiedTypeName(utype) + ")"
                + currentExpr + ").is"
                + JavaEmitter.makeAccessor(choice.getName()) + "()) ";
    }

    public void castTo(TypeInterface srctype, TypeInterface dsttype)
    {
        if (srctype != dsttype)
        {
            currentExpr = "(" + JavaEmitter.printType(dsttype) + ")" + "("
                    + currentExpr + ")";
        }
    }

    /*
     * stack is: String array <- top Parameter parameter
     */
    public void forall(Node exprbody, ArrayType atype)
    {
        String arrayname = pop();
        String parameter = pop();
        String jname = (String) forallExpr2Name.get(exprbody);
        if (jname == null)
        {
            jname = "forall_" + forallCounter++;
            fexpr.addElement(new forallExpr(jname, parameter, arrayname,
                    exprbody, atype));
            forallExpr2Name.put(exprbody, jname);
        }
        currentExpr = jname + "(__cc)";
    }

    static int forallCounter = 0;
    static Vector fexpr = new Vector();
    static HashMap forallExpr2Name = new HashMap();

    static String emitForalls(CompoundType ctype, String indent)
    {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        indent += "  ";
        for (int i = 0; i < fexpr.size(); i++)
        {
            forallExpr f = (forallExpr) fexpr.elementAt(i);
            out.println(indent + "boolean " + f.jname + "("
                    + JavaEmitter.CALL_CHAIN_TYPE + " __cc) {");
            out.println(indent + "  boolean __rc = true;");
            out.println(indent + "  try {");
            ExpressionTypeCheckVisitor jvis;
            JavaExpressionEmitter em;
            // what if lowerbound != 0 ?
            String lb = "0", ubl = f.arrayname + ".length()", idx = f.param;
            if (f.atype.lowerBound != null)
            {
                lb = JavaEmitter
                        .getLowerBound(f.atype, ctype.getScope(), ctype);
                ubl = lb + " + " + ubl;
            }
            out.println(indent + "    for (int " + idx + " = (int)(" + lb
                    + "); " + idx + " < " + ubl + "; " + idx + "++) {");

            em = new JavaExpressionEmitter(ctype, ctype.getScope());
            jvis = new ExpressionTypeCheckVisitor(ctype, ctype.getScope(), em);
            f.exprbody.accept(jvis);
            out.println(indent + "      __rc = __rc && " + em.result() + ";");
            out.println(indent + "    }");
            out.println(indent + "    if (false) throw new "
                    + JavaEmitter.IO_EXCEPTION + "(); /* placate compiler */");

            out.println(indent + "    return __rc;");
            out.println(indent
                    + "  } catch (ArrayIndexOutOfBoundsException _) {");
            out.println(indent + "    return false;");
            out.println(indent + "  } catch (" + JavaEmitter.IO_EXCEPTION
                    + " __) {");
            out.println(indent + "    return false;");
            out.println(indent + "  }");
            out.println(indent + "}");
        }
        fexpr = new Vector();
        out.close(); // needed for StringWriter?
        return sw.toString();
    }

    class forallExpr
    {
        String jname, arrayname, param;
        Node exprbody;
        ArrayType atype;

        forallExpr(String jname, String param, String arrayname, Node exprbody,
                ArrayType atype)
        {
            this.jname = jname;
            this.param = param;
            this.atype = atype;
            this.exprbody = exprbody;
            this.arrayname = arrayname;
        }
    }

    public void unaryOperation(NodeToken n)
    {
        currentExpr = n.tokenImage + currentExpr;
    }

    /**
     * left op is on stack right op is current expression
     */
    public void binaryOperation(NodeToken n)
    {
        currentExpr = pop() + " " + n.tokenImage + " " + currentExpr;
    }

    /**
     * first op is on stack second op is on stack third op is current expression
     */
    public void ternaryOperation(NodeToken n0, NodeToken n1)
    {
        String a = pop();
        currentExpr = pop() + " " + n0.tokenImage + " " + a + " "
                + n1.tokenImage + " " + currentExpr;
    }

    public void parenthesize()
    {
        currentExpr = "(" + currentExpr + ")";
    }
}
