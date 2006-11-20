/*
 * Main.java
 *
 * @author: Godmar Back
 * @version: $Id: Main.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript;
import datascript.parser.*;
import datascript.visitor.*;
import datascript.syntaxtree.Node;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    static void assertThat(boolean b) {
	if (!b) {
	    new Exception("Assertion failed:").printStackTrace(System.out);
	    System.exit(-1);
	}
    }

    static String printNode(Node n) {
	 StringWriter sw = new StringWriter();
	 TreeDumper dumper = new datascript.visitor.TreeDumper(sw);
	 dumper.startAtNextToken();
	 n.accept(dumper);
	 String out = sw.toString();
	 while (out.startsWith(" ")) {
	    out = out.substring(1);
	 }
	 return out;
    }

    static void debug(String s) {
	if (debug) {
	    System.out.println("<DEBUG:" + s + ">");
	}
    }

    static void warn(String s) {
	if (warn) {
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
    static PrintWriter containedgraph = null;
    static String pkgPrefix = null;
    static String destDir = null;
    
    //? additional boolean
    static boolean emitSqlite3 = true;
    static boolean emitJavaBytes = true;

    private static void help() {
       System.err.println("Usage: ... flags file(s)\n"
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
	 );
    }

    public static void main(String av[]) throws Exception  {
	InputStream in;
	boolean doStdin = true;

	for (int i = 0; i < av.length; i++) {
	    if (av[i].equals("-ci")) {
		containedgraph = new PrintWriter(new FileOutputStream(av[++i]));
		containedgraph.println("digraph contained_in_graph {");
		containedgraph.println("\tnode[fontname = \"Arial\", "
		      	+ "fontsize=12];");
	    } else
	    if (av[i].equals("-dt")) {
		dumptree = true;
	    } else
	    if (av[i].equals("-se")) {
		shortexceptions = true;
	    } else
	    if (av[i].equals("-d")) {
		destDir = av[++i];
	    } else
	    if (av[i].equals("-pkg")) {
		pkgPrefix = av[++i];
	    } else
	    if (av[i].equals("-po")) {
		parseonly = true;
	    } else
	    if (av[i].equals("-do")) {
		debugoutput = true;
	    } else
	    if (av[i].equals("-nosc")) {
		stringconstructor = false;
	    } else
	    if (av[i].equals("-nv")) {
		emitvisitors = false;
	    } else
	    if (av[i].equals("-ov")) {
		overwritefiles = true;
	    } else
	    if (av[i].equals("-dp")) {
		debugparser = true;
	    } else
	    if (av[i].equals("-warn")) {
		warn = true;
	    } else
	    if (av[i].equals("-debug")) {
		debug = true;
	    } else
	    if (av[i].equals("-em")) {
		emitconstraints = true;
	    } else {
	        if (av[i].startsWith("-")) {
		   help();
		   System.exit(-1);
		}
//	        debugparser = true;
//	        debugoutput = true;
	        
		try {
		    compileFile(new java.io.FileInputStream(av[i]), av[i]);
		    doStdin = false;
		} catch(java.io.FileNotFoundException e){
		    System.err.println(av[i] + " not found.");
		    return ;
		}
	    }
	}
	if (doStdin) {
	  // compileFile(System.in, "<stdin>");
		compileFile(System.in, "<stdin>");
	}
        if (containedgraph != null) {
	   containedgraph.println("}");
	   containedgraph.close();
        }
    }

    static void compileFile(InputStream in, String filename) {
        LineError.currentFile = filename;
	DS parser = new DS(in);

	try {
	    if (debugparser) {
		parser.enable_tracing();
	    } else {
		parser.disable_tracing();
	    }
	    
	    datascript.syntaxtree.TranslationUnit root;
	    root = parser.TranslationUnit();
	    System.err.println(filename + " parsed successfully.");
	    //? ^ Type checking, It's not generating anyting.
	    if (parseonly) {
	        return;
	    }
	    if (dumptree) {
		root.accept(new datascript.visitor.TreeDumper(System.out));
		//? TreeDumper( given by JTB.jar ), Dump tree.
	    }

	    StructType globals = new StructType("Global", null);
	    globals.setByteOrder(TypeInterface.DEFAULT_BYTE_ORDER);
	    Scope globalScope = new Scope();
	    globals.setScope(globalScope);
	    //? Define Structure type "globals", It is root of parsing tree.
	    
	    /*
	    for (int i = 0; i < Condition.builtinConditions.length; i++) {
	       Condition c = Condition.builtinConditions[i];
	       globalScope.setSymbol(c.getName(), c);
	    }
	    */

	    root.accept(new TypeEvaluator(globals), globals.getScope());
	    //? Excute TypeEvaluator( globals.getScope() ).
	    System.err.println(filename + " checking pass 1");
	    globals.link(); // linking to Scopes
	    globals.resolveFieldTypes();
	    System.err.println(filename + " checking pass 1 completed.");
	    System.err.println(filename + " checking pass 2");
	    ExpressionTypeCheckVisitor.checkExpressions(globals);
	    System.err.println(filename + " checking pass 2 completed.");
	    if (emitconstraints) {
	    	int suffix = filename.indexOf('.');
	    	String basename = filename.substring(0, suffix);
	    	PrintWriter ofile = openFile(basename + ".cs");
	    	ConstraintsEmitter c = new ConstraintsEmitter(ofile, basename);
	    	c.emit(globals);
	    	System.err.println(filename + " Constraints emitted.");
	    }
	    if (true) {
	    	int suffix = filename.lastIndexOf(".ds");
	    	if (suffix == -1 && pkgPrefix == null) {
		    System.err.println("Error: don't know where to write java code for " + filename);
		    System.err.println("Please use .ds suffix or specify -pkg"); 
		    return;
		}

//		 emiter phase. we don't need to change source code in above.
		
		String pkgname = pkgPrefix != null ?  pkgPrefix + ".Files": filename.substring(0, suffix) + ".Files";
		String dirname = pkgname.replace('.', File.separatorChar);
		File dir = new File(destDir != null ? (destDir + File.separator +  dirname) :  dirname);
		if (!dir.exists()) {
		    if (!dir.mkdirs()) {
			throw new Error("Could not create directory: " + dir.toString());
		    }
		}
        
		
		JavaEmitter e = new JavaEmitter(dirname, pkgname);
		e.emit(globals);

		if (emitvisitors) {
		    new JavaEmitVisitors(e).emit(globals);
		}

		new JavaEmitMembershipTests(e).emit(globals);

	
		//? new emitter class for sqlite.
		pkgname = pkgPrefix != null ?  pkgPrefix + ".sql3": filename.substring(0, suffix) + ".sql3";
		dirname = pkgname.replace('.', File.separatorChar); 
		dir = new File(destDir != null ? (destDir + File.separator +  dirname) :  dirname);
		if (!dir.exists()) {
		    if (!dir.mkdirs()) {
			throw new Error("Could not create directory: " + dir.toString());
		    }
		}
		
		
		String rootpkgname = filename.substring(0,suffix);
		if(emitSqlite3 == true ){
			Sqlite3Emitter sql_e = new Sqlite3Emitter(dirname, pkgname , rootpkgname);
			sql_e.emit(globals);
		}
		
//		? new emitter class for sqlite.
		pkgname = pkgPrefix != null ?  pkgPrefix + ".Bytes": filename.substring(0, suffix) + ".Bytes";
		dirname = pkgname.replace('.', File.separatorChar); 
		dir = new File(destDir != null ? (destDir + File.separator +  dirname) :  dirname);
		if (!dir.exists()) {
		    if (!dir.mkdirs()) {
			throw new Error("Could not create directory: " + dir.toString());
		    }
		}
		
		JavaBytesEmitter java_bytes_e = new JavaBytesEmitter(dirname, pkgname);
		java_bytes_e.emit(globals);
		if(emitJavaBytes == true ){
			
			
			if (emitvisitors) {
			    new JavaBytesEmitVisitors(java_bytes_e).emit(globals);
			}

			
		}
		new JavaBytesEmitMembershipTests(java_bytes_e).emit(globals);
		
	
		
		
		String cp = System.getProperty("java.class.path");
		// System.err.println("cp=" + cp);

		int idx = cp.indexOf("datascript.jar");
		if (idx != -1) {
		    int lb = cp.lastIndexOf(':', idx) + 1;
		    int rb = cp.indexOf(':', idx);
		    if (rb == -1) rb = cp.length();
		    cp = cp.substring(lb, rb);
		}

		System.err.println("Done.\n" +
		    "Compile with javac -classpath " + cp + " " + dirname + "/*.java");
	    }
	} catch(Throwable e) {
	    if (debug) {
	       e.printStackTrace(System.err);
	    } else {
	       System.err.println(e);
	    }
	}
    }

    /* open a file relative to destdir, return null if file can't be opened or overwritten */
    static PrintWriter openFile(String filename) {
	try  {
	    File file = new File(destDir != null ? (destDir + File.separator + filename) : filename);
	    if (file.exists()) {
		if (!Main.overwritefiles) {
		    System.err.println(file + " exists, not overwriting");
		    return null;
		} else {
		    System.err.println(file + " exists, overwriting");
		}
	    }
	    return new PrintWriter(new FileOutputStream(file));
	} catch (IOException e) {
	    System.err.println(e);
	}
	return null;
    }
}

