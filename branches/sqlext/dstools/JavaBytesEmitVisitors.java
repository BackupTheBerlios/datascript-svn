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
import java.util.HashMap;
import java.util.Iterator;

import datascript.parser.DSConstants;

class JavaBytesEmitVisitors
{
    JavaBytesEmitter javaBytesEmitter;

    JavaBytesEmitVisitors(JavaBytesEmitter javaBytesEmitter)
    {
        this.javaBytesEmitter = javaBytesEmitter;
    }

    void emit(CompoundType ctype) throws IOException
    {
        emitVisitorInterface(ctype);
        emitVisitorImplementation(ctype);
    }

    void emitVisitorInterface(CompoundType ctype) throws IOException
    {
        PrintWriter out = Main.openFile(javaBytesEmitter.dirname + File.separator
                + javaBytesEmitter.VISITOR + ".java");
        if (out == null)
        {
            return;
        }

        javaBytesEmitter.writeHeader(out);
        out.println("public interface " + javaBytesEmitter.VISITOR + " {");
        emitInterfaceMethods(out, ctype);
        emitBuiltinTypes(out, ";");

        out.println("  interface " + javaBytesEmitter.ACCEPTOR + " {");
        out.println("    public void accept(" + javaBytesEmitter.VISITOR
                + " visitor);");
        out.println("  }");
        out.println();

        out.println("}");
        out.close();
    }

    private void emitFunctionHeader(PrintWriter out, CompoundType ctype)
    {
        out.print("  public void visit("
                + JavaBytesEmitter.fullyQualifiedTypeName(ctype) + " n)");
    }

    private void emitInterfaceMethods(PrintWriter out, CompoundType ctype)
    {
        Iterator children = ctype.getNestedTypes();
        while (children.hasNext())
        {
            ctype = (CompoundType) children.next();
            if (ctype.isEmpty())
            {
                continue;
            }
            emitFunctionHeader(out, ctype);
            out.println(";");
            emitInterfaceMethods(out, ctype);
        }
    }

    void emitVisitorImplementation(CompoundType ctype) throws IOException
    {
        PrintWriter out = Main.openFile(javaBytesEmitter.dirname + File.separator
                + javaBytesEmitter.DEPTHFIRST + ".java");
        if (out == null)
        {
            return;
        }
        javaBytesEmitter.writeHeader(out);
        out.println();
        out.println("public class " + javaBytesEmitter.DEPTHFIRST + " implements "
                + javaBytesEmitter.VISITOR + " {");
        emitDepthFirstMethods(out, ctype);
        emitBuiltinTypes(out, " { }");
        out.println("}");
        out.close();
    }

    void emitBuiltinTypes(PrintWriter out, String suffix)
    {
        int[] t = { DSConstants.INT8, DSConstants.INT16, DSConstants.INT32,
                DSConstants.INT64, DSConstants.LEINT16, DSConstants.LEINT32,
                DSConstants.LEINT64, DSConstants.UINT8, DSConstants.UINT16,
                DSConstants.UINT32, DSConstants.UINT64, DSConstants.LEUINT16,
                DSConstants.LEUINT32, DSConstants.LEUINT64 };

        for (int i = 0; i < t.length; i++)
        {
            TypeInterface type = BuiltinType.getTypeByTokenKind(t[i]);
            out.println("  public void visit" + type + "("
                    + javaBytesEmitter.printType(type) + " n)" + suffix);
        }

        String[] bftypes = new String[] { "byte", "short", "int", "long",
                "java.math.BigInteger" };

        for (int i = 0; i < bftypes.length; i++)
        {
            out.println("  public void visitBitField(" + bftypes[i]
                    + " n, int length)" + suffix);
        }
        out.println("  public void visitString(String n)" + suffix);
    }

    private void emitDepthFirstMethods(PrintWriter out, CompoundType ctype)
    {
        Iterator children = ctype.getNestedTypes();
        while (children.hasNext())
        {
            ctype = (CompoundType) children.next();
            if (ctype.isEmpty())
            {
                continue;
            }
            HashMap fieldName2armName = null;
            boolean isUnion = ctype instanceof UnionType;
            if (isUnion)
            {
                fieldName2armName = javaBytesEmitter.getArmNameMapping(ctype);
                if (fieldName2armName == null)
                {
                    throw new InternalError(
                            "field name or type mapping unknown");
                }
            }

            emitFunctionHeader(out, ctype);
            out.println(" {");
            if (isUnion)
            {
                out.println("    switch(n." + javaBytesEmitter.CHOICETAG + ") {");
            }
            Iterator fields = ctype.getFields();
            while (fields.hasNext())
            {
                Field f = (Field) fields.next();
                TypeInterface ftype = f.getType();
                String fname = f.getName();
                String fullTypeName = null;
                String ind = "    ";
                if (isUnion)
                {
                    out.println("    case "
                            + javaBytesEmitter.fullyQualifiedTypeName(ctype)
                            + ".CHOICE_" + fname + ":");
                    fname = (String) fieldName2armName.get(fname);
                    ind += "  ";
                }

                boolean optional = (f.getOptionalClause() != null);
                String oldIndent = ind;
                if (optional)
                {
                    out.println(ind + "if (n.has"
                            + javaBytesEmitter.makeAccessor(fname) + "())");
                    out.println(ind + "{");
                    ind += "  ";
                }

                if (ftype instanceof TypeInstantiation)
                {
                    ftype = ((TypeInstantiation) ftype).baseType;
                }

                if (ftype instanceof CompoundType)
                {
                    CompoundType fctype = (CompoundType) ftype;
                    String cast = "";
                    if (isUnion)
                    {
                        String tname = javaBytesEmitter
                                .fullyQualifiedTypeName(fctype);
                        cast = "(" + tname + ")";
                    }

                    if (!fctype.isEmpty())
                    {
                        out.println(ind + "(" + cast + "n." + fname
                                + ").accept(this);");
                    }
                }
                else if (ftype instanceof ArrayType)
                {
                    ArrayType atype = (ArrayType) ftype;
                    TypeInterface etype = atype.getElementType();
                    out.println(ind + "for (int i = 0; i < n." + fname
                            + ".length(); i++) {");
                    try
                    {
                        BuiltinType btype = BuiltinType.getBuiltinType(etype);
                        String cast = "";
                        if (btype instanceof BitFieldType
                                && ((BitFieldType) btype).isVariable())
                        {
                            cast = "(BigInteger)";
                        }
                        out.print(ind
                                + "  visit"
                                + btype
                                + "("
                                + cast
                                + "("
                                + (isUnion ? "(" + javaBytesEmitter.printType(ftype)
                                        + ")" : "") + "n." + fname
                                + ").elementAt(i)");
                        if (btype instanceof BitFieldType)
                        {
                            out
                                    .print(", "
                                            + ((BitFieldType) btype)
                                                    .getLength());
                        }
                        out.println(");");
                    }
                    catch (ClassCastException _)
                    {
                        if (etype instanceof StringType)
                        {
                            out.println(ind + "  visitString(((String)" + "(("
                                    + javaBytesEmitter.RUNTIME_PKG
                                    + "ObjectArray)n." + fname
                                    + ").elementAt(i)));");
                        }
                        else
                        {
                            out.println(ind + "  ((" + javaBytesEmitter.FQ_ACCEPTOR
                                    + ")" + "((" + javaBytesEmitter.RUNTIME_PKG
                                    + "ObjectArray)n." + fname
                                    + ").elementAt(i))" + ".accept(this);");
                        }
                    }
                    out.println(ind + "}");
                }
                else if (ftype instanceof StringType)
                {
                    out.println(ind + "visitString" + "(n." + fname + ");");
                }
                else
                {
                    BuiltinType btype = BuiltinType.getBuiltinType(ftype);
                    out.print(ind + "visit" + btype + "(n." + fname);
                    if (btype instanceof BitFieldType)
                    {
                        out.print(", " + ((BitFieldType) btype).getLength());
                    }
                    out.println(");");
                }
                if (isUnion)
                {
                    out.println(ind + "break;");
                }
                if (optional)
                {
                    ind = oldIndent;
                    out.println(ind + "}");
                }
            }
            if (isUnion)
            {
                out.println("    }");
            }
            out.println("  }");
            out.println();
            emitDepthFirstMethods(out, ctype);
        }
    }
}
