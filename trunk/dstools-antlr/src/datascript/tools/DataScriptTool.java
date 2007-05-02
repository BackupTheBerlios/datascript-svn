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
import java.io.FileInputStream;
import java.util.HashSet;

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

import datascript.ast.DataScriptException;
import datascript.ast.ParserException;
import datascript.ast.Scope;
import datascript.ast.TokenAST;

import datascript.emit.XmlDumperEmitter;
import datascript.emit.html.ContentEmitter;
import datascript.emit.html.FramesetEmitter;
import datascript.emit.html.CssEmitter;
import datascript.emit.html.PackageEmitter;
import datascript.emit.html.OverviewEmitter;
import datascript.emit.java.DepthFirstVisitorEmitter;
import datascript.emit.java.JavaEmitter;
import datascript.emit.java.SizeOfEmitter;
import datascript.emit.java.VisitorEmitter;


public class DataScriptTool 
{
    private static final String VERSION = "rds 0.82alpha (02 May 2007)";
    private ToolContext context;
    private TokenAST rootNode;

    private String packageName = null;
    private String fileName = null;
    private String pathName = null;
    private String outPathName = null;
    private boolean generateDocs = false;
    private boolean checkSyntax = false;
    private HashSet<String> allPackageFiles = new HashSet<String>(); 



    public void parseArguments(String[] args) throws DataScriptException
    {
    	for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-pkg"))
            {
                packageName = args[++i];
            }
            else if (args[i].equals("-doc"))
            {
                generateDocs = true;
            }
            else if (args[i].equals("-c"))
            {
                checkSyntax = true;
            }
            else if (args[i].equals("-path"))
            {
                pathName = args[++i];
            }
            else if (args[i].equals("-out"))
            {
                outPathName = args[++i];
            }
            else
            {
                fileName = args[i];
            }
        }
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

        if (fileName == null /*|| packageName == null*/)
        {
            final String NL = System.getProperties().getProperty("line.separator");
            final StringBuilder buffer = new StringBuilder();

            buffer.append("parameter missing." + NL + NL);
            buffer.append("rds [-doc] [-c] [-out \"pathname for output\"] [-pkg \"packagename\"] [-path \"pathname\"] \"filename\"" + NL);
            buffer.append("usage: " + NL);
            buffer.append(" -doc\tgenerates Javadoc-style documentation" + NL);
            buffer.append(" -c\tchecks syntax" + NL);
            buffer.append(" -out \"pathname\"\tdefines the path to the directory in witch the generated code is stored" + NL);
            buffer.append(" -pkg \"packagename\"\tdefines the default packagename" + NL);
            buffer.append(" -path \"pathname\"\tdefines the path to datascript files" + NL);
            buffer.append(" \"filename\"\tdefines the main datascript file" + NL);

            throw new DataScriptException(buffer.toString());
        }
    }


    public void parseDatascript() throws Exception
    {
        // create tool context for information exchange between pipeline
        // components
        context = ToolContext.getInstance();
        context.setFileName(fileName);
        context.setPathName(pathName);

        rootNode = (TokenAST) parsePackage();
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
        
        parseImportedPackages(rootNode);


        // Validate the syntax tree - this has no side effects.
        if (checkSyntax)
        {
            DataScriptWalker walker = new DataScriptWalker();
            walker.setContext(context);
            walker.translationUnit(rootNode);
            if (context.getErrorCount() != 0)
                throw new ParserException("Parser errors.");
        }

        // create name scopes and resolve references
        TypeEvaluator typeEval = new TypeEvaluator();
        typeEval.setContext(context);

        Scope globals = new Scope();
        typeEval.pushScope(globals);
        typeEval.translationUnit(rootNode);
        globals.link(null);
        
        // check expression types and evaluate constant expressions
        ExpressionEvaluator exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.translationUnit(rootNode);
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
    }


    public void emitJava(DataScriptEmitter emitter) throws Exception
    {
        System.out.println("emitting java code");

        // emit Java code for decoders
        JavaEmitter javaEmitter = new JavaEmitter(outPathName, packageName, (AST)rootNode);
        javaEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(javaEmitter);
        emitter.translationUnit(rootNode);

        // emit Java __Visitor interface
        VisitorEmitter visitorEmitter = new VisitorEmitter(outPathName, packageName, (AST)rootNode);
        visitorEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(visitorEmitter);
        emitter.translationUnit(rootNode);

        // emit Java __DepthFirstVisitor class
        DepthFirstVisitorEmitter dfVisitorEmitter = 
            new DepthFirstVisitorEmitter(outPathName, packageName, (AST)rootNode);
        dfVisitorEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(dfVisitorEmitter);
        emitter.translationUnit(rootNode);

        // emit Java __SizeOf class
        SizeOfEmitter sizeOfEmitter = new SizeOfEmitter(outPathName, packageName, (AST)rootNode);
        sizeOfEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(sizeOfEmitter);
        emitter.translationUnit(rootNode);

        // emit Java __XmlDumper class
        XmlDumperEmitter xmlDumper = new XmlDumperEmitter(outPathName, packageName, (AST)rootNode);
        xmlDumper.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(xmlDumper);
        emitter.translationUnit(rootNode);
    }


    public void emitHTML(DataScriptEmitter emitter) throws Exception
    {
        System.out.println("emitting html documentation");
/**
 * TODO: new HTML generating
 */
        // emit HTML documentation
        ContentEmitter htmlEmitter = new ContentEmitter();
        htmlEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(htmlEmitter);
        emitter.translationUnit(rootNode);

        // emit frameset
        FramesetEmitter framesetEmitter = new FramesetEmitter();
        framesetEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(framesetEmitter);
        emitter.translationUnit(rootNode);

        // emit stylesheeds
        CssEmitter cssEmitter = new CssEmitter();
        cssEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(cssEmitter);
        emitter.translationUnit(rootNode);

        // emit list of packages
        PackageEmitter packageEmitter = new PackageEmitter();
        packageEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(packageEmitter);
        emitter.translationUnit(rootNode);

        // emit list of classes
        OverviewEmitter overviewEmitter = new OverviewEmitter();
        overviewEmitter.setPackageName(rootNode.getFirstChild());
        emitter.setEmitter(overviewEmitter);
        emitter.translationUnit(rootNode);
    }


    private void parseImportedPackages(AST rootNode) throws Exception
    {
        AST node = rootNode.getFirstChild();
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
                    AST packageRoot = parsePackage();

                    mergeSyntaxTrees(rootNode, packageRoot);
                }

                AST child = node.getFirstChild();
                while(child != null && child.getType() != DataScriptParserTokenTypes.ROOT)
                    child = child.getNextSibling();
                if (child != null)
                    parseImportedPackages(child);
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
        lexer.setTokenObjectClass("datascript.ast.FileNameToken");
        TokenBuffer buffer = new TokenBuffer(lexer);
        DataScriptParser parser = new DataScriptParser(buffer);
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);
        
        // use custom node class containing line information
        parser.setASTNodeClass("datascript.ast.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");

        return parser.getAST();
    }



    private void mergeSyntaxTrees(AST rootNode, AST packageRoot)
    {
        AST rootMembers = getImportNode(rootNode, getPackageNode(packageRoot));
        if (rootMembers == null)
        {
            rootMembers = getMembersNode(rootNode);
        	AST importedMembers = getMembersNode(packageRoot);
        	rootMembers.addChild(importedMembers.getFirstChild());
       	}
       	else
       	    rootMembers.addChild(packageRoot);
    }


    private AST getMembersNode(AST root)
    {
        AST node = root.getFirstChild();
        while (node != null && node.getType() != DataScriptParserTokenTypes.MEMBERS)
        {
            node = node.getNextSibling();
        }        
        return node;
    }


    private AST getPackageNode(AST root)
    {
        AST node = root.getFirstChild();
        while (node != null && node.getType() != DataScriptParserTokenTypes.PACKAGE)
        {
            node = node.getNextSibling();
        }
        return node;
    }


    private AST getImportNode(AST root, AST packageNode)
    {
        if (packageNode == null)
            return null;
        AST node = root.getFirstChild();
        while (true)
        {
            while (node != null && node.getType() != DataScriptParserTokenTypes.IMPORT)
            {
                node = node.getNextSibling();
            }
    
            if (node != null)
            {
                AST pn = packageNode.getFirstChild();
                AST in = node.getFirstChild();
                while (in != null && pn != null && in.getText().equals(pn.getText()))
                {
                    pn = pn.getNextSibling();
                    in = in.getNextSibling();
                }
                if (in == null && pn == null)
                    break;
                node = node.getNextSibling();
            }
        }
        return node;
    }


    public static void main(String[] args)
    {
        System.out.println(VERSION);
        DataScriptTool dsTool = new DataScriptTool();
        try
        {
            dsTool.parseArguments(args);
            dsTool.parseDatascript();

            DataScriptEmitter emitter = new DataScriptEmitter();
            dsTool.emitJava(emitter);
            if (dsTool.generateDocs)
                dsTool.emitHTML(emitter);
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
