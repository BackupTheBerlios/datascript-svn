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
import datascript.emit.html.HtmlEmitter;
import datascript.emit.java.DepthFirstVisitorEmitter;
import datascript.emit.java.JavaEmitter;
import datascript.emit.java.SizeOfEmitter;
import datascript.emit.java.VisitorEmitter;

public class DataScriptTool 
{
    private static final String VERSION = "rds 0.71 (5 Mar 2007)";
    private ToolContext context;
    private DataScriptWalker walker;
    private TypeEvaluator typeEval;
    private ExpressionEvaluator exprEval;
    private TokenAST rootNode;
    private Scope globals;
    private JavaEmitter javaEmitter;
    private DataScriptEmitter emitter;
    private VisitorEmitter visitorEmitter;
    private DepthFirstVisitorEmitter dfVisitorEmitter;
    private SizeOfEmitter sizeOfEmitter;

    private String packageName = null;
    private String fileName = null;
    private boolean generateDocs = false;
    
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
            else
            {
                fileName = args[i];
            }
        }

        if (fileName == null || packageName == null)
        {
            final String NL = System.getProperties().getProperty("line.separator");
            final StringBuilder buffer = new StringBuilder();

            buffer.append("parameter missing." + NL + NL);
            buffer.append("rds [-doc] -pkg \"packagename\" \"path to DataScript.ds\"" + NL);
            buffer.append("usage: " + NL);
            buffer.append(" -doc\tgenerates Javadoc-style documentation" + NL);
            buffer.append(" -pkg \"packagename\"\tdefines the packagename" + NL);
            buffer.append(" \"path to DataScript.ds\"\tdefines the DataScript input file" + NL);

            throw new DataScriptException(buffer.toString());
        }
    }
    
    public void parseDatascript() throws Exception
    {
        // create tool context for information exchange between pipeline
        // components
        context = ToolContext.getInstance();
        context.setFileName(fileName);

        rootNode = (TokenAST) parsePackage(fileName);
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");
        
        parseImportedPackages();

        // Validate the syntax tree - this has no side effects.
        // TODO: make this optional, controlled by a command line option
        walker = new DataScriptWalker();
        walker.setContext(context);
        walker.translationUnit(rootNode);

        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");

        // create name scopes and resolve references
        typeEval = new TypeEvaluator();
        typeEval.setContext(context);
        globals = new Scope();
        typeEval.pushScope(globals);
        typeEval.translationUnit(rootNode);
        globals.link(null);
        
        // check expression types and evaluate constant expressions
        exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.translationUnit(rootNode);
        
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");

        // emit Java code for decoders
        javaEmitter = new JavaEmitter();
        javaEmitter.setPackageName(packageName);
        emitter = new DataScriptEmitter();
        emitter.setEmitter(javaEmitter);
        emitter.translationUnit(rootNode);

        // emit Java __Visitor interface
        visitorEmitter = new VisitorEmitter();
        visitorEmitter.setPackageName(packageName);
        emitter.setEmitter(visitorEmitter);
        emitter.translationUnit(rootNode);        

        // emit Java __DepthFirstVisitor class
        dfVisitorEmitter = new DepthFirstVisitorEmitter();
        dfVisitorEmitter.setPackageName(packageName);
        emitter.setEmitter(dfVisitorEmitter);
        emitter.translationUnit(rootNode);        

        // emit Java __SizeOf class
        sizeOfEmitter = new SizeOfEmitter();
        sizeOfEmitter.setPackageName(packageName);
        emitter.setEmitter(sizeOfEmitter);
        emitter.translationUnit(rootNode);        

        // emit Java __XmlDumper class
        XmlDumperEmitter xmlDumper = new XmlDumperEmitter();
        xmlDumper.setPackageName(packageName);
        emitter.setEmitter(xmlDumper);
        emitter.translationUnit(rootNode);        

        if (generateDocs)
        {
            System.out.println("Generating html documentation");
            // emit HTML documentation
            HtmlEmitter htmlEmitter = new HtmlEmitter();
            htmlEmitter.setPackageName(packageName);
            emitter.setEmitter(htmlEmitter);
            emitter.translationUnit(rootNode);
        }
    }
    
    private void parseImportedPackages() throws Exception
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
                AST packageRoot = parsePackage(fileName);
                
                mergeSyntaxTrees(rootNode, packageRoot);
            }
        }
    }
    
    private String getPackageFile(AST node)
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
    
    private AST parsePackage(String fileName) throws Exception
    {
        System.out.println("Parsing " + fileName);

        context = ToolContext.getInstance();
        context.setFileName(fileName);

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
        AST rootMembers = getMembersNode(rootNode);
        AST importedMembers = getMembersNode(packageRoot);
        rootMembers.addChild(importedMembers.getFirstChild());
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

    public static void main(String[] args)
    {
        System.out.println(VERSION);
        DataScriptTool dsTool = new DataScriptTool();
        try
        {
            dsTool.parseArguments(args);
            dsTool.parseDatascript();
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
        System.out.println("parsing finished");
    }
}
