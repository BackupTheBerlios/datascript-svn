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

import datascript.syntaxtree.Node;

class ConstraintsEmitter
{
    PrintWriter pout;
    String basename; // as in [basename].ds
    static String shiftWidth = "    ";
    static String lineCommentChar = "%";

    ConstraintsEmitter(PrintWriter out, String basename)
    {
        this.pout = out;
        this.basename = basename;
    }

    void emit(CompoundType type)
    {
        pout.println(lineCommentChar + " Constraints Emitter output:");
        pout.println(lineCommentChar + shiftWidth + " File :" + basename
                + ".cs");
        pout.println(lineCommentChar);
        pout.println();
        emitCVC(type);
        pout.flush();
    }

    void emitCVC(CompoundType type)
    {
        ConstraintsListCVC l = new ConstraintsListCVC(pout, type);
        l.printCVC();
    }

    class ConstraintsListCVC extends ConstraintsList
    {
        ConstraintsListCVC(PrintWriter out, CompoundType type)
        {
            super(out, type);
        }

        ConstraintsListCVC(PrintWriter out, CompoundType type, String base,
                Constraint c, ConstraintsList n, ConstraintsList unionList)
        {
            super(out, type, base, c, n, unionList);
        }

        ConstraintsList makeList(PrintWriter out, CompoundType type,
                String base, Constraint c, ConstraintsList n, ConstraintsList un)
        {
            return new ConstraintsListCVC(out, type, base, c, n, un);
        }

        Constraint makeConstraint(CompoundType type, Field f, Node n,
                String base)
        {
            return new ConstraintCVC(type, f, n, base, symbols);
        }

        Constraint makeConstraint(String expr, CompoundType type, String base)
        {
            return new ConstraintCVC(expr, type, base, symbols);
        }

        public void printCVC()
        {
            printSymbols();
            print();
            this.out.println("QUERY FALSE;");
        }

        public String getCVCString(Constraint c)
        {
            if (c.getCvcType())
                return "ASSERT (" + c + ");";
            else
                return "QUERY NOT (" + c + ");";
        }

        public void print()
        {
            if (cnst != null)
                out.println(getCVCString(cnst));
            if (next != null)
                next.print();
        }
    }

    class ConstraintCVC extends Constraint
    {
        ConstraintCVC(CompoundType type, Field f, Node n, String base, HashMap s)
        {
            super(type, f, n, base, s);
        }

        ConstraintCVC(String str, CompoundType type, String base, HashMap s)
        {
            super(str, type, base, s);
        }

        public String getOperator(String op)
        {
            if (op.equals("=="))
                return " = ";
            if (op.equals("&&"))
                return " AND ";
            if (op.equals("||"))
                return " OR ";
            if (op.equals("!="))
                return " /= ";
            if (op.equals("!"))
                return " NOT ";
            return op;
        }
    }
}
