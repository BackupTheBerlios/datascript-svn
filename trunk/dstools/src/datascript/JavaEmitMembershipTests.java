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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import datascript.parser.DSConstants;

class JavaEmitMembershipTests
{
    String basename;
    JavaEmitter javaEmitter;

    // basename for compound type, used during traversal
    private String cbasename;

    JavaEmitMembershipTests(JavaEmitter javaEmitter)
    {
        this.javaEmitter = javaEmitter;
    }

    void emit(CompoundType ctype) throws IOException
    {
        PrintWriter out = Main.openFile(javaEmitter.dirname + File.separator
                + javaEmitter.CHECKTHAT + ".java");

        if (out == null)
        {
            return;
        }
        javaEmitter.writeHeader(out);
        out.println("public class " + javaEmitter.CHECKTHAT + " {");
        emitChildren(out, ctype);
        out.println("}");
        out.close();
    }

    void emitChildren(PrintWriter out, CompoundType ctype) throws IOException
    {
        emitSetTypeMembershipTests(out, ctype);
        Iterator children = ctype.getNestedTypes();
        while (children.hasNext())
        {
            ctype = (CompoundType) children.next();
            emitChildren(out, ctype);
        }
    }

    private void emitEnumTypeMembershipTests(PrintWriter out, EnumType etype,
            CompoundType ctype)
    {
        Value[] val = etype.getValues();
        String ename = cbasename + etype.getName();
        String etname = JavaEmitter.printType(etype.getType());
        String mname = ename + "_members";

        out.println("  private static " + etname + " []" + mname + " = new "
                + etname + "[] {\n    ");
        for (int i = 0; i < val.length; i++)
        {
            JavaExpressionEmitter em = new JavaExpressionEmitter(ctype);
            em.value(val[i]);
            em.castTo(BuiltinType.getTypeByTokenKind(DSConstants.INT32), etype
                    .getType());
            out.print(em.result());
            if (i < val.length - 1)
            {
                out.print(", ");
            }
        }
        out.println("  };");
        out.println("  static boolean isValid_" + ename + "(" + etname
                + " x) {");
        out.println("    return java.util.Arrays.binarySearch(" + mname
                + ", x) >= 0;");
        out.println("  }");
    }

    private void emitBitmaskTypeMembershipTests(PrintWriter out,
            BitmaskType btype)
    {
        IntegerValue mask = btype.getMask();
        String bname = cbasename + btype.getName();
        String etname = JavaEmitter.printType(btype.getType());

        String mname = bname + "_mask";
        out.println("  private static " + etname + " " + mname + " = 0x"
                + mask.integerValue().toString(16) + ";");
        out.println("  static boolean isValid_" + bname + "(" + etname
                + " x) {");
        out.println("    return (" + etname + ")(" + mname + " | x) == "
                + mname + ";");
        out.println("  }");
    }

    static String getFullPath(CompoundType ctype)
    {
        String fname = JavaEmitter.fullyQualifiedTypeName(ctype);
        return fname.replace('.', '_') + "_";
    }

    /**
     * emit test methods for membership to an enum
     */
    private void emitSetTypeMembershipTests(PrintWriter out, CompoundType ctype)
    {
        Iterator stypes = ctype.getSetTypes();
        if (!stypes.hasNext())
        {
            return;
        }
        cbasename = getFullPath(ctype);
        out.println("\n  /* emitting checks for " + cbasename + " */");

        while (stypes.hasNext())
        {
            SetType stype = (SetType) stypes.next();
            Main.assertThat(stype.getCompound() == ctype);
            if (stype instanceof EnumType)
            {
                emitEnumTypeMembershipTests(out, (EnumType) stype, ctype);
            }
            else
            {
                emitBitmaskTypeMembershipTests(out, (BitmaskType) stype);
            }
        }
    }
}
