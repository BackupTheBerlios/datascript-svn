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
import java.io.FileInputStream;

import antlr.TokenBuffer;
import antlr.TokenStreamRecognitionException;
import datascript.antlr.DataScriptEmitter;
import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptWalker;
import datascript.antlr.ExpressionEvaluator;
import datascript.antlr.TypeEvaluator;
import datascript.ast.Scope;
import datascript.ast.TokenAST;
import datascript.emit.Emitter;
import datascript.emit.java.JavaEmitter;

public class DataScriptSyntaxTreeTest 
{
    private ToolContext context;
    private DataScriptParser parser;
    private DataScriptWalker walker;
    private TypeEvaluator typeEval;
    private ExpressionEvaluator exprEval;
    private TokenAST rootNode;
    private Scope globals;
    private JavaEmitter console;
    private DataScriptEmitter emitter;
    
    public void parseDatascript(String fileName) throws Exception
    {
        // create tool context for information exchange between pipeline
        // components
        context = ToolContext.getInstance();
        context.setFileName(fileName);
        // set up lexer, parser and token buffer
        FileInputStream is = new FileInputStream(fileName); 
        DataScriptLexer lexer = new DataScriptLexer(is);
        lexer.setFilename(fileName);
        TokenBuffer buffer = new TokenBuffer(lexer);
        parser = new DataScriptParser(buffer);
        //lexer.setContext(context);
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);
        
        // use custom node class containing line information
        parser.setASTNodeClass("datascript.ast.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        rootNode = (TokenAST) parser.getAST();

        // validate the syntax tree - this has no side effects
        if (context.getErrorCount() == 0)
        {
            walker = new DataScriptWalker();
            walker.setContext(context);
            walker.translationUnit(rootNode);
        }

        // create name scopes and resolve references
        typeEval = new TypeEvaluator();
        typeEval.setContext(context);
        globals = new Scope();
        typeEval.pushScope(globals);
        typeEval.translationUnit(rootNode);
        globals.link(null);
        
        exprEval = new ExpressionEvaluator();
        exprEval.setContext(context);
        exprEval.pushScope(globals);
        exprEval.translationUnit(rootNode);
        
        console = new JavaEmitter();
        console.setPackageName("bits");
        emitter = new DataScriptEmitter();
        emitter.setEmitter(console);
        emitter.translationUnit(rootNode);
        
        
    }

    public static void main(String[] args)
    {
        String fileName = args[0];
        DataScriptSyntaxTreeTest dsTool = new DataScriptSyntaxTreeTest();
        try
        {
            dsTool.parseDatascript(fileName);
            //ASTFrame frame = new ASTFrame("AST", dsTool.rootNode);
            //frame.setVisible(true);            
        }
        catch (TokenStreamRecognitionException exc)
        {
            System.err.println(exc);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }
}
