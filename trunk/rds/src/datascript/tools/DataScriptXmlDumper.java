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
import java.io.IOException;
import java.util.HashSet;

import antlr.Token;
import antlr.TokenStreamHiddenTokenFilter;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.util.FileNameToken;
import datascript.antlr.util.TokenAST;
import datascript.antlr.util.ToolContext;
import datascript.ast.ParserException;



public class DataScriptXmlDumper implements Parameters
{
    private TokenAST rootNode = null;
    private DataScriptParser parser;
    private ToolContext context;
    private HashSet<String> allPackageFiles = new HashSet<String>();


    public DataScriptXmlDumper()
    {
        Token token = new FileNameToken(DataScriptParserTokenTypes.ROOT, "ROOT");
        rootNode = new TokenAST(token);
    }


    private void parseImportedPackages(AST unitNode) throws Exception
    {
        AST node = unitNode.getFirstChild();
        if (node.getType() == DataScriptParserTokenTypes.PACKAGE)
        {
            while (true)
            {
                node = node.getNextSibling();
                if (node == null
                        || node.getType() != DataScriptParserTokenTypes.IMPORT)
                    break;

                String fileName = getPackageFile(node);
                if (!allPackageFiles.contains(fileName))
                {
                    allPackageFiles.add(fileName);
                    context.setFileName(fileName);
                    AST unitRoot = parsePackage();
                    if (unitRoot != null)
                    {
                        rootNode.addChild(unitRoot);
                        parseImportedPackages(unitRoot);
                    }
                }
            }
        }
    }


    private static String getPackageFile(AST node)
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
            parser.setContext(context);
    
            // must call this to see file name in error messages
            parser.setFilename(fileName);
    
            // use custom node class containing line information
            parser.setASTNodeClass("datascript.antlr.util.TokenAST");
        }
        catch (java.io.FileNotFoundException fnfe)
        {
            ToolContext.logError((TokenAST)parser.getAST(), fnfe.getMessage());
        }

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


    public static void printXml(TokenAST ast) throws IOException
    {
        // print out this node and all siblings
        for (AST node = ast; node != null; node = node.getNextSibling())
        {
            if (node.getFirstChild() == null)
            {
                // print guts (class name, attributes)
                System.out.println("<ID>" + node.getText() + "</ID>");
            }
            else
            {
                String text = node.getText();
                if (text.length() > 0)
                {
                    if (!Character.isLetter(text.charAt(0)))
                    {
                        text = "op";
                    }
                    System.out.println("<" + text + ">");
                }

                // print children
                printXml((TokenAST) node.getFirstChild());

                // print end tag
                System.out.println("</" + node.getText() + ">");
            }
        }
    }


    public void emitDatascript() throws Exception
    {
        /* if the XML extension is present, generate xml dump */
        try
        {
            Class clazz = Class.forName("datascript.backend.xml.XmlExtension");
            Extension extension = (Extension) clazz.newInstance();
            extension.setParameter(this);
            extension.generate(null, rootNode);
        }
        catch (ClassNotFoundException e)
        {
            System.err
                    .println("Extension datascript.backend.xml.XmlExtension not found, nothing emitted.");
        }
    }


    /** ****** Implementation of Parameters interface ******* */


    public String getVersion()
    {
        return null;
    }


    public String getDefaultPackageName()
    {
        return null;
    }


    public boolean getGenerateDocs()
    {
        return false;
    }


    public boolean getCheckSyntax()
    {
        return true;
    }


    public String getPathName()
    {
        return ".";
    }


    public String getOutPathName()
    {
        return ".";
    }


    public String getFileName()
    {
        return ToolContext.getFileName();
    }


    public DataScriptParser getParser()
    {
        return parser;
    }


    public boolean argumentExists(String key)
    {
        return false;
    }


    public String getCommandlineArg(String key) throws Exception
    {
        return null;
    }


    /** ****** End of Parameters interface ******* */

    public static void main(String[] args)
    {
        try
        {
            DataScriptXmlDumper dsTool = new DataScriptXmlDumper();
            dsTool.context = ToolContext.getInstance();
            int i = 0;
            // dsTool.context.setPathName(args[i++]);
            dsTool.context.setFileName(args[i++]);
            AST unitRoot = (TokenAST) dsTool.parsePackage();
            dsTool.rootNode.addChild(unitRoot);
            dsTool.parseImportedPackages(unitRoot);

            /*
             OutputStreamWriter osw = new OutputStreamWriter(System.out);
             dsTool.rootNode.xmlSerialize(osw);
             osw.flush();
             printXml(dsTool.rootNode);
             //System.out.println(dsTool.rootNode.toStringList());
             //*/

            ASTFrame frame = new ASTFrame("AST", dsTool.rootNode);
            frame.setVisible(true);
            dsTool.emitDatascript();
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
