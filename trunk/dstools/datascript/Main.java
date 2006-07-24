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

import datascript.parser.*;
import datascript.visitor.*;
import datascript.syntaxtree.Node;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main
{
    static String VERSION = "0.4 (17 Jul 2006)";

    static void assertThat(boolean b)
    {
        if (!b)
        {
            new Exception("Assertion failed:").printStackTrace(System.out);
            System.exit(-1);
        }
    }

    static String printNode(Node n)
    {
        StringWriter sw = new StringWriter();
        TreeDumper dumper = new datascript.visitor.TreeDumper(sw);
        dumper.startAtNextToken();
        n.accept(dumper);
        String out = sw.toString();
        while (out.startsWith(" "))
        {
            out = out.substring(1);
        }
        return out;
    }

    static void debug(String s)
    {
        if (debug)
        {
            System.out.println("<DEBUG:" + s + ">");
        }
    }

    static void warn(String s)
    {
        if (warn)
        {
            System.out.println("<WARNING:" + s + ">");
        }
    }

    static boolean debug = false;
    static boolean warn = false;
    static boolean dumptree = false;
    static boolean debugparser = false;
    static boolean debugoutput = false;
    static boolean parseonly = false;
    static boolean shortexceptions = false;
    static boolean emitvisitors = true;
    static boolean overwritefiles = false;
    static boolean emitconstraints = false;
    static boolean stringconstructor = true;
    static boolean emitXml = false;
    static boolean emitXmlVisitor = false;
    static PrintWriter containedgraph = null;
    static String pkgPrefix = null;
    static String destDir = null;

    private static void help()
    {
        System.err
                .println("Usage: ... flags file(s)\n"
                        + " -ov             overwrite files (<<<IMPORTANT>>>)\n"
                        + " -do             create code for debugging\n"
                        + " -ci filename    print 'contained-in' graph as dot\n"
                        + " -pkg a.b.c      put output files in package a.b.c (default is basename of .ds file)\n"
                        + " -d destdir      write output in destdir/a/b/c\n"
                        + " -dt             dump parse tree\n"
                        + " -se             use short exceptions (smaller code)\n"
                        + " -po             parse only\n"
                        + " -nosc           don't create constructor(String s) (for conflicts)\n"
                        + " -nv             do not emit visitors\n"
                        + " -dp             debug parser\n"
                        + " -warn           emit warnings\n"
                        + " -debug          turn on general debugging\n"
                        + " -em             emit constraints in <basename>.cs\n"
                        + " -xml            emit XML dump in <basename>.xml\n"
                        + " -xv             emit XML visitor\n");
    }

    public static void main(String av[]) throws Exception
    {
        InputStream in;
        boolean doStdin = true;

        for (int i = 0; i < av.length; i++)
        {
            if (av[i].equals("-ci"))
            {
                containedgraph = new PrintWriter(new FileOutputStream(av[++i]));
                containedgraph.println("digraph contained_in_graph {");
                containedgraph.println("\tnode[fontname = \"Arial\", "
                        + "fontsize=12];");
            }
            else if (av[i].equals("-dt"))
            {
                dumptree = true;
            }
            else if (av[i].equals("-se"))
            {
                shortexceptions = true;
            }
            else if (av[i].equals("-d"))
            {
                destDir = av[++i];
            }
            else if (av[i].equals("-pkg"))
            {
                pkgPrefix = av[++i];
            }
            else if (av[i].equals("-po"))
            {
                parseonly = true;
            }
            else if (av[i].equals("-do"))
            {
                debugoutput = true;
            }
            else if (av[i].equals("-nosc"))
            {
                stringconstructor = false;
            }
            else if (av[i].equals("-nv"))
            {
                emitvisitors = false;
            }
            else if (av[i].equals("-ov"))
            {
                overwritefiles = true;
            }
            else if (av[i].equals("-dp"))
            {
                debugparser = true;
            }
            else if (av[i].equals("-xml"))
            {
                emitXml = true;
            }
            else if (av[i].equals("-xv"))
            {
                emitXmlVisitor = true;
            }
            else if (av[i].equals("-warn"))
            {
                warn = true;
            }
            else if (av[i].equals("-debug"))
            {
                debug = true;
            }
            else if (av[i].equals("-em"))
            {
                emitconstraints = true;
            }
            else
            {
                if (av[i].startsWith("-"))
                {
                    help();
                    System.exit(-1);
                }
                try
                {
                    compileFile(new java.io.FileInputStream(av[i]), av[i]);
                    doStdin = false;
                }
                catch (java.io.FileNotFoundException e)
                {
                    System.err.println(av[i] + " not found.");
                    return;
                }
            }
        }
        if (doStdin)
        {
            compileFile(System.in, "<stdin>");
        }
        if (containedgraph != null)
        {
            containedgraph.println("}");
            containedgraph.close();
        }
    }

    static void compileFile(InputStream in, String filename)
    {
        LineError.currentFile = filename;
        DS parser = new DS(in);

        try
        {
            if (debugparser)
            {
                parser.enable_tracing();
            }
            else
            {
                parser.disable_tracing();
            }
            datascript.syntaxtree.TranslationUnit root;
            root = parser.TranslationUnit();
            System.err.println("datascript " + VERSION);
            System.err.println(filename + " parsed successfully.");
            if (parseonly)
            {
                return;
            }
            if (dumptree)
            {
                root.accept(new datascript.visitor.TreeDumper(System.out));
            }

            StructType globals = new StructType("Global", null, null);
            globals.setByteOrder(TypeInterface.DEFAULT_BYTE_ORDER);
            Scope globalScope = new Scope();
            globals.setScope(globalScope);
            /*
             * for (int i = 0; i < Condition.builtinConditions.length; i++) {
             * Condition c = Condition.builtinConditions[i];
             * globalScope.setSymbol(c.getName(), c); }
             */

            root.accept(new TypeEvaluator(globals), globals.getScope());
            System.err.println(filename + " checking pass 1");
            globals.link();
            globals.resolveFieldTypes();
            System.err.println(filename + " checking pass 1 completed.");
            System.err.println(filename + " checking pass 2");
            ExpressionTypeCheckVisitor.checkExpressions(globals);
            System.err.println(filename + " checking pass 2 completed.");
            if (emitconstraints)
            {
                int suffix = filename.indexOf('.');
                String basename = filename.substring(0, suffix);
                PrintWriter ofile = openFile(basename + ".cs");
                ConstraintsEmitter c = new ConstraintsEmitter(ofile, basename);
                c.emit(globals);
                System.err.println(filename + " Constraints emitted.");
            }
            if (true)
            {
                int suffix = filename.lastIndexOf(".ds");
                if (suffix == -1 && pkgPrefix == null)
                {
                    System.err
                            .println("Error: don't know where to write java code for "
                                    + filename);
                    System.err.println("Please use .ds suffix or specify -pkg");
                    return;
                }

                String pkgname = pkgPrefix != null ? pkgPrefix : filename
                        .substring(0, suffix);
                String dirname = pkgname.replace('.', File.separatorChar);
                File dir = new File(
                        destDir != null ? (destDir + File.separator + dirname)
                                : dirname);
                if (!dir.exists())
                {
                    if (!dir.mkdirs())
                    {
                        throw new Error("Could not create directory: "
                                + dir.toString());
                    }
                }

                JavaEmitter e = new JavaEmitter(dirname, pkgname);
                e.emit(globals);

                if (emitvisitors)
                {
                    new JavaEmitVisitors(e).emit(globals);
                }

                new JavaEmitMembershipTests(e).emit(globals);
                if (emitXml)
                {
                    suffix = filename.indexOf('.');
                    String basename = filename.substring(0, suffix);
                    PrintWriter out = openFile(basename + ".xml");
                    new XmlEmitter(out).emitContainedTypes(globals);
                    out.close();
                }
                if (emitXmlVisitor)
                {
                    PrintWriter out = openFile(dirname + File.separator
                            + "__XmlDumper.java");
                    new XmlDumperEmitter(e, out, pkgname).emit(globals);
                    out.close();
                }
                String cp = System.getProperty("java.class.path");
                // System.err.println("cp=" + cp);

                int idx = cp.indexOf("datascript.jar");
                if (idx != -1)
                {
                    int lb = cp.lastIndexOf(':', idx) + 1;
                    int rb = cp.indexOf(':', idx);
                    if (rb == -1)
                        rb = cp.length();
                    cp = cp.substring(lb, rb);
                }

                System.err.println("Done.\n" + "Compile with javac -classpath "
                        + cp + " " + dirname + "/*.java");
            }
        }
        catch (Throwable e)
        {
            if (debug)
            {
                e.printStackTrace(System.err);
            }
            else
            {
                System.err.println(e);
                System.exit(1);
            }
        }
    }

    /*
     * open a file relative to destdir, return null if file can't be opened or
     * overwritten
     */
    static PrintWriter openFile(String filename)
    {
        try
        {
            File file = new File(
                    destDir != null ? (destDir + File.separator + filename)
                            : filename);
            if (file.exists())
            {
                if (!Main.overwritefiles)
                {
                    System.err.println(file + " exists, not overwriting");
                    return null;
                }
                else
                {
                    System.err.println(file + " exists, overwriting");
                }
            }
            return new PrintWriter(new FileOutputStream(file));
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
        return null;
    }
}
