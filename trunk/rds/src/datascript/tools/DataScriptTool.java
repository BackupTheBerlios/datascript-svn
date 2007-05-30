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
package datascript.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;

import antlr.TokenBuffer;
import antlr.TokenStreamRecognitionException;
import antlr.collections.AST;

import datascript.antlr.DataScriptEmitter;
import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.DataScriptWalker;
import datascript.antlr.ExpressionEvaluator;
import datascript.antlr.TypeEvaluator;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;

import datascript.ast.DataScriptException;
import datascript.ast.ParserException;
import datascript.ast.Package;
import datascript.ast.Scope;


public class DataScriptTool implements Parameters
{
    private static final String VERSION = "rds 0.9.1 (29 May 2007)";
    private static final File EXT_DIR = new File("ext/");
    private ToolContext context;
    private TokenAST rootNode = null;
    private DataScriptParser parser = null;
    private Scope globals = new Scope();
    private final HashSet<String> allPackageFiles = new HashSet<String>();

    private DataScriptEmitter emitter = new DataScriptEmitter();

    /* Properties for command line parameters */
    private final HashMap<String, String> cmdLineArgs = new HashMap<String, String>();
    private String defaultPackageName = "";
    private String fileName = null;
    private String srcPathName = null;
    private String outPathName = null;
    private boolean generateDocs = false;
    private boolean checkSyntax = false; 



    public DataScriptTool()
    {
        rootNode = new TokenAST(new antlr.Token(DataScriptParserTokenTypes.ROOT));
    }


    public void parseArguments(String[] args) throws DataScriptException
    {
        int i = 0;
        while (i < args.length-1)
        {
            String key = args[i++];
            String value = (i < args.length-1)? args[i++] : null;
            if (value != null && value.startsWith("-"))
            {
                value = null;
                i--;
            }
            cmdLineArgs.put(key, value);
        }
        fileName = args[i];

        defaultPackageName = cmdLineArgs.get("-pkg");
        generateDocs = cmdLineArgs.containsKey("-doc");
        checkSyntax = cmdLineArgs.containsKey("-c");
        srcPathName = cmdLineArgs.get("-src");
        outPathName = cmdLineArgs.get("-out");

        if (outPathName == null || outPathName.length() == 0)
        {
            outPathName = ".";
        }
        else
        {
            i = outPathName.length();
            while (outPathName.charAt(i-1) == File.separatorChar)
                --i;
            if (i < outPathName.length())
                outPathName = outPathName.substring(0, i);
        }

        if (fileName == null)
        {
            final String NL = System.getProperties().getProperty("line.separator");
            final StringBuilder buffer = new StringBuilder();

            buffer.append("parameter missing." + NL + NL);
            buffer.append("rds [-doc] [-c] [-ext \"pathname to extensions\"] [-out \"pathname for output\"] [-pkg \"packagename\"] [-src \"pathname\"] \"filename\"" + NL);
            buffer.append("usage: " + NL);
            buffer.append(" -doc\tgenerates Javadoc-style documentation" + NL);
            buffer.append(" -c\tchecks syntax" + NL);
            buffer.append(" -ext \"pathname to extensions\"\tpath to the plugin-directory" + NL);
            buffer.append(" -out \"pathname\"\tpath to the directory in which the generated code is stored" + NL);
            buffer.append(" -pkg \"packagename\"\tJava package name for types without a DataScript package" + NL);
            buffer.append(" -src \"pathname\"\tpath to DataScript source files" + NL);
            buffer.append(" \"filename\"\tmain DataScript source file" + NL);

            throw new DataScriptException(buffer.toString());
        }
    }


    public void parseDatascript() throws Exception
    {
        // create tool context for information exchange between pipeline
        // components
        context = ToolContext.getInstance();
        context.setFileName(fileName);
        context.setPathName(srcPathName);

        allPackageFiles.add(fileName);
        AST unitRoot = (TokenAST) parsePackage();
        rootNode.addChild(unitRoot);
        parseImportedPackages(unitRoot);


        // Validate the syntax tree - this has no side effects.
        if (checkSyntax)
        {
            DataScriptWalker walker = new DataScriptWalker();
            walker.setContext(context);
            walker.root(rootNode);
            if (context.getErrorCount() != 0)
                throw new ParserException("Parser errors.");
        }

        // create name scopes and resolve references
        TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.setContext(context);
        typeEval.pushScope(globals);
        typeEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
        Package.linkAll();
        
        // check expression types and evaluate constant expressions
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
    }


    public void emitDatascript() throws Exception
    {
        String extDirName = cmdLineArgs.get("-ext");
        File myExtDir = (extDirName != null && extDirName.length() > 0)? new File(extDirName) : EXT_DIR;
        Collection<File> extensionFiles = findExtensionsRecursively(myExtDir);
        if (extensionFiles == null)
        {
            System.out.println("No backends in " + EXT_DIR.getAbsolutePath() + " found, nothing emitted.");
            return;
        }
        Collection<URL> urls = toURLs((File[]) extensionFiles.toArray(new File[] {}));
        ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[] {}));

        for (File f : extensionFiles)
        {
            java.util.jar.JarFile rf = new java.util.jar.JarFile(f);
            java.util.jar.Manifest m = rf.getManifest();
            if (m != null)
            {
                String mc = m.getMainAttributes().getValue("Main-Class");
                try
                {
                    Class clazz = Class.forName(mc, true, classLoader);
                    Extension extension = (Extension) clazz.newInstance();
                    extension.setParameter(this);
                    extension.generate(emitter, rootNode);
                }
                catch (ClassNotFoundException e)
                {
                    System.err.println("Extension " + mc + " not found, nothing emitted.");
                }
            }
        }
    }


    private Collection<File> findExtensionsRecursively(File directory)
    {
        if (directory == null || !directory.isDirectory())
            return null;
        Collection<File> retVal = new ArrayList<File>();

        File[] extensionFiles = directory.listFiles( 
                new FileFilter()
                { 
                    public boolean accept(File file)
                    {
                        return file.isDirectory() || 
                               ( file.getName().endsWith("Extension.jar") &&
                                 file.getName().startsWith("rds"));
                    }
                }
            );

        for (File file : extensionFiles)
        {
            if (file.isDirectory())
                retVal.addAll(findExtensionsRecursively(file));
            if (file.isFile())
                retVal.add(file);
        }
        return retVal;
    }


    private Collection<URL> toURLs(File[] files)
    {
        Collection<URL> urls = new ArrayList<URL>(files.length);
        for (File file : files)
        {
            try
            {
                urls.add(new URL("jar:file:" + file.getPath() + "!/"));
            }
            catch (MalformedURLException e)
            {
                System.err.println("Could not load " + file.getPath());
                System.err.println(e.toString());
            }
        }
        return urls;
    }


    private void parseImportedPackages(AST unitNode) throws Exception
    {        
        AST node = unitNode.getFirstChild();
        if (node.getType() == DataScriptParserTokenTypes.PACKAGE)
        {
            while (true)
            {
                node = node.getNextSibling();
                if (node == null || 
                        node.getType() != DataScriptParserTokenTypes.IMPORT)
                    break;
                
                String fileName = getPackageFile(node);
                if (!allPackageFiles.contains(fileName))
                {
                    allPackageFiles.add(fileName);
                    context.setFileName(fileName);
                    AST unitRoot = parsePackage();
                    rootNode.addChild(unitRoot);
                    parseImportedPackages(unitRoot);
                }
            }
        }
    }
    


    public static String getPackageFile(AST node)
    {
        AST sibling = node.getFirstChild();
        String fileName = sibling.getText();
        File file = new File(fileName);
        while (true)
        {
            sibling = sibling.getNextSibling();
            if (sibling == null)
                break;
            
            file = new File(file, sibling.getText());
        }
        return file.getPath() + ".ds";
    }



    private AST parsePackage() throws Exception
    {
        String fileName = ToolContext.getFullName();
        System.out.println("Parsing " + fileName);

        // set up lexer, parser and token buffer
        FileInputStream is = new FileInputStream(fileName); 
        DataScriptLexer lexer = new DataScriptLexer(is);
        lexer.setFilename(fileName);
        lexer.setTokenObjectClass("datascript.antlr.util.FileNameToken");
        TokenBuffer buffer = new TokenBuffer(lexer);
        parser = new DataScriptParser(buffer);
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);
        
        // use custom node class containing line information
        parser.setASTNodeClass("datascript.antlr.util.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
        
        // TODO: Log warning if package name AST does not match file name
        return parser.getAST();
    }


    /******** Implementation of Parameters interface ********/


    public String getDefaultPackageName()
    {
        return defaultPackageName;
    }


    public boolean getGenerateDocs()
    {
        return generateDocs;
    }


    public boolean getCheckSyntax()
    {
        return checkSyntax;
    }


    public String getPathName()
    {
        return srcPathName;
    }


    public String getOutPathName()
    {
        return outPathName;
    }


    public String getFileName()
    {
        return fileName;
    }


    public DataScriptParser getParser()
    {
        return parser;
    }


    public String getCommandLineArg(String key)
    {
        return cmdLineArgs.get(key);
    }


    /******** End of Parameters interface ********/


    public static void main(String[] args)
    {
        System.out.println(VERSION);
        DataScriptTool dsTool = new DataScriptTool();
        try
        {
            dsTool.parseArguments(args);
            dsTool.parseDatascript();
            dsTool.emitDatascript();
        }
        catch (DataScriptException exc)
        {
            System.err.println(exc);
        }
        catch (TokenStreamRecognitionException exc)
        {
            System.err.println(exc);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        System.out.println("finished");
    }
}
