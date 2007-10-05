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
import java.util.HashMap;
import java.util.HashSet;

import antlr.Token;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.TokenStreamRecognitionException;
import antlr.collections.AST;
import datascript.antlr.DataScriptEmitter;
import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.DataScriptWalker;
import datascript.antlr.ExpressionEvaluator;
import datascript.antlr.TypeEvaluator;
import datascript.antlr.util.FileNameToken;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;
import datascript.ast.DataScriptException;
import datascript.ast.Package;
import datascript.ast.ParserException;
import datascript.ast.Scope;


public class DataScriptTool implements Parameters
{
    private static final String VERSION = "rds 0.15.2 (5 Oct 2007)";

    private static final File EXT_DIR = new File("ext/");
    private ToolContext context;
    private TokenAST rootNode = null;
    private DataScriptParser parser = null;
    private Scope globals = new Scope();
    private final HashSet<String> allPackageFiles = new HashSet<String>();
    private final ArrayList<Extension> extensions = new ArrayList<Extension>();

    private DataScriptEmitter emitter = new DataScriptEmitter();

    /* Properties for command line parameters */
    private final HashMap<String, String> cmdLineArgs = new HashMap<String, String>();
    private String fileName = null;
    private String srcPathName = null;
    private String outPathName = null;
    private boolean checkSyntax = false; 



    public DataScriptTool()
    {
        Token token = new FileNameToken(DataScriptParserTokenTypes.ROOT, "ROOT");
        rootNode = new TokenAST(token);
    }


    public void parseArguments(String[] args) throws DataScriptException
    {
        // TODO: Das Fehlen des Dateinamen wird nicht erkannt.
        int i = -1;
        while (i < args.length-1)
        {
            String key = args[++i];
            if (!key.startsWith("-"))
            {
                if (i != args.length-1)
                    continue;
                // this must be the mainfile name
                fileName = new File(key).getPath();
            }

            if (i < args.length -1)
            {
                String value = args[++i];
                if (value.startsWith("-"))  // this is a new key, not a value
                {
                    value = null;
                    i--;
                }
                cmdLineArgs.put(key, value);
            }
        }
    }


    public boolean checkArguments()
    {
        checkSyntax = cmdLineArgs.containsKey("-c");
        srcPathName = cmdLineArgs.get("-src");
        outPathName = cmdLineArgs.get("-out");

        if (outPathName == null || outPathName.length() == 0)
        {
            outPathName = ".";
        }
        else
        {
            int i = outPathName.length();
            while (outPathName.charAt(i-1) == File.separatorChar)
                --i;
            if (i < outPathName.length())
                outPathName = outPathName.substring(0, i);
        }

        if (fileName == null)
        {
            printUsage();
        }
        return true;
    }


    private void printUsage()
    {
        final String NL = System.getProperties().getProperty("line.separator");
        final StringBuilder buffer = new StringBuilder();

        buffer.append("parameter missing." + NL + NL);

        buffer.append("rds <extension options> [-c] [-ext \"pathname to extensions\"] [-out \"pathname for output\"] [-src \"pathname\"] \"filename\"" + NL);
        buffer.append("usage: " + NL);

        for (Extension extension : extensions)
        {
            buffer.append(extension.getUsage());
        }

        buffer.append(NL);
        buffer.append(" -c\t\t\tcheck syntax" + NL);
        buffer.append(" -ext \"pathname\"\tpath to the extension directory" + NL);
        buffer.append(" -out \"pathname\"\tpath to the directory in which the generated code is stored" + NL);
        buffer.append(" -src \"pathname\"\tpath to DataScript source files" + NL);
        buffer.append(" \"filename\"\t\tmain DataScript source file" + NL);

        throw new DataScriptException(buffer.toString());
    }


    private void prepareExtensions() throws Exception
    {
        String extDirName = cmdLineArgs.get("-ext");
        File myExtDir = (extDirName != null && extDirName.length() > 0)? new File(extDirName) : EXT_DIR;
        Collection<File> extensionFiles = findExtensionsRecursively(myExtDir);
        if (extensionFiles == null || extensionFiles.size() <= 0)
            return;

        Collection<URL> urls = toURLs((File[]) extensionFiles.toArray(new File[] {}));
        ClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[] {}), this.getClass().getClassLoader());

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
                    extensions.add(extension);
                }
                catch (ClassNotFoundException e)
                {
                    System.err.println("Extension " + mc + " not found.");
                }
            }
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
        if (unitRoot != null)
        {
            rootNode.addChild(unitRoot);
            parseImportedPackages(unitRoot);
        }


        // Validate the syntax tree - this has no side effects.
        if (checkSyntax)
        {
            DataScriptWalker walker = new DataScriptWalker();
            walker.setContext(context);
            walker.root(rootNode);
            if (context.getErrorCount() != 0)
                throw new ParserException("Walker: Parser errors.");
        }

        // create name scopes and resolve references
        TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.pushScope(globals);
        typeEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("TypeEvaluator: Parser errors.");
        Package.linkAll();
        if (ToolContext.getInstance().getErrorCount() != 0)
            throw new ParserException("TypeEvaluator: Linker errors.");
        
        // check expression types and evaluate constant expressions
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.root(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("ExpressionEvaluator: Parser errors.");
    }


    public void emitDatascript() throws Exception
    {
        if (rootNode == null)
            return;

        if (extensions == null || extensions.size() <= 0)
        {
            System.out.println("No backends found in " + EXT_DIR.getAbsolutePath() + ", nothing emitted.");
            return;
        }
        for (Extension extension : extensions)
        {
            extension.generate(emitter, rootNode);
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
            {
                Collection<File> eFiles = findExtensionsRecursively(file);
                if (eFiles != null && eFiles.size() > 0)
                    retVal.addAll(eFiles);
            }
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
                    AST unitRoot = (TokenAST) parsePackage();
                    if (unitRoot != null)
                    {
                        rootNode.addChild(unitRoot);
                        parseImportedPackages(unitRoot);
                    }
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
        try
        {
            FileInputStream is = new FileInputStream(fileName); 
            DataScriptLexer lexer = new DataScriptLexer(is);
            lexer.setFilename(fileName);
            lexer.setTokenObjectClass("datascript.antlr.util.FileNameToken");
            TokenStreamHiddenTokenFilter filter = new TokenStreamHiddenTokenFilter(lexer);
            filter.discard(DataScriptParserTokenTypes.WS);
            filter.discard(DataScriptParserTokenTypes.COMMENT);
            filter.hide(DataScriptParserTokenTypes.DOC);
            parser = new DataScriptParser(filter);
        }
        catch (java.io.FileNotFoundException fnfe)
        {
            ToolContext.logError((parser == null)? null : (TokenAST)parser.getAST(), fnfe.getMessage());
        }

        if (parser == null)
            return null;
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);
        
        // use custom node class containing line information
        parser.setASTNodeClass("datascript.antlr.util.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        AST retVal = parser.getAST();
        if (context.getErrorCount() != 0 || retVal == null)
            throw new ParserException("DataSciptParser: Parser errors.");

        String pkgName = ToolContext.getFileName();
        pkgName = pkgName.substring(0, pkgName.lastIndexOf(".ds"));
        TokenAST node = (TokenAST)retVal.getFirstChild();
        if (node.getType() != DataScriptParserTokenTypes.PACKAGE || node.getText().equals(pkgName))
            ToolContext.logWarning(node, "filename and packeage name do not match!");
        return retVal;
    }


    /******** Implementation of Parameters interface ********/


    public String getVersion()
    {
        return VERSION;
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


    public boolean argumentExists(String key)
    {
        return cmdLineArgs.containsKey(key);
    }


    public String getCommandlineArg(String key) throws Exception
    {
        if (!cmdLineArgs.containsKey(key))
            throw new Exception(key + " is non of the commandline arguments.");
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
            dsTool.prepareExtensions();
            dsTool.checkArguments();

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
        System.out.println("done.");
    }
}
