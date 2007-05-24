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
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashSet;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import antlr.ANTLRException;
import antlr.TokenBuffer;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;

import datascript.antlr.DataScriptLexer;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;
import datascript.antlr.DocCommentParserTokenTypes;
import datascript.ast.ParserException;
import datascript.ast.TokenAST;


public class DataScriptXmlDumper extends XMLFilterImpl
{
    private ContentHandler handler;
    private AttributesImpl noAttr = new AttributesImpl();
    private TokenAST rootNode = new TokenAST(new antlr.Token(DataScriptParserTokenTypes.ROOT));
    private DataScriptParser parser;
    private ToolContext context;
    private HashSet<String> allPackageFiles = new HashSet<String>();


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
        FileInputStream is = new FileInputStream(fileName); 
        DataScriptLexer lexer = new DataScriptLexer(is);
        lexer.setFilename(fileName);
        lexer.setTokenObjectClass("datascript.ast.FileNameToken");
        TokenBuffer buffer = new TokenBuffer(lexer);
        parser = new DataScriptParser(buffer);
        parser.setContext(context);

        // must call this to see file name in error messages
        parser.setFilename(fileName);

        // use custom node class containing line information
        parser.setASTNodeClass("datascript.ast.TokenAST");

        // parse file and get root node of syntax tree
        parser.translationUnit();
        if (context.getErrorCount() != 0)
            throw new ParserException("Parser errors.");

        // TODO: Log warning if package name AST does not match file name
        return parser.getAST();
    }


    /**************************************************************/


    private void startElement(String tag) throws SAXException
    {
        handler.startElement("", "", tag, noAttr);
    }


    private void endElement(String tag) throws SAXException
    {
        handler.endElement("", "", tag);
    }


    private void text(String s) throws SAXException
    {
        handler.characters(s.toCharArray(), 0, s.length());
    }


    private void handleDocNode(AST node) throws SAXException
    {
        StringReader is = new StringReader(node.getText());
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer buffer = new TokenBuffer(lexer);
        DocCommentParser parser = new DocCommentParser(buffer);
        try
        {
            parser.comment();
            AST docNode = parser.getAST();
            startElement("DOC");
            AST child = docNode.getFirstChild();
            for (; child != null; child = child.getNextSibling())
            {
                switch (child.getType())
                {
                    case DocCommentParserTokenTypes.TEXT:
                    {
                        startElement("P");
                        text(child.getText());
                        AST text = child.getFirstChild();
                        for (; text != null; text = text.getNextSibling())
                        {
                            text(text.getText());
                        }
                        endElement("P");
                        break;
                    }

                    case DocCommentParserTokenTypes.AT:
                    {
                        String tag = child.getText().toUpperCase();
                        startElement(tag);
                        AST text = child.getFirstChild();
                        for (; text != null; text = text.getNextSibling())
                        {
                            text(text.getText());
                        }
                        endElement(tag);
                        break;
                    }
                }
            }
            endElement("DOC");
        }
        catch (ANTLRException exc)
        {
            System.err.println("XXXX" + node.getText());
        }
    }


    private void handleDataNode(AST node) throws SAXException
    {
        boolean literal = false;
        String tokenName = parser.getTokenName(node.getType());
        if (tokenName.charAt(0) == '"')
        {
            tokenName = tokenName.substring(1, tokenName.length()-1).toUpperCase();
            literal = true;
        }
        if (node.getFirstChild() == null)
        {
            if (literal)
            {
                startElement(tokenName);
                endElement(tokenName);                    
            }
            else
            {
                startElement(tokenName);
                text(node.getText());
                endElement(tokenName);
            }
        }
        else
        {
            startElement(tokenName);
            // print children
            fireSaxEvents((TokenAST)node.getFirstChild());
            endElement(tokenName);
        }
    }


    public void fireSaxEvents(TokenAST ast) throws SAXException
    {
        // print out this node and all siblings
        for (AST node = ast; node != null; node = node.getNextSibling())
        {
            switch (node.getType())
            {
                case DataScriptParserTokenTypes.DOC:
                {
                    try
                    {
                        handleDocNode(node);
                    }
                    catch (Exception exc)
                    {
                        throw new RuntimeException(exc);
                    }
                    break;
                }

                default:
                {
                    handleDataNode(node);
                }
            }
        }
    }


    /*
     * 'main' for xml output
     * @see org.xml.sax.helpers.XMLFilterImpl#parse(org.xml.sax.InputSource)
     */
    public void parse(InputSource is) throws SAXException
    {
        handler = getContentHandler();
        handler.startDocument();
        fireSaxEvents((TokenAST) rootNode);
        handler.endDocument();
    }


    /******************************************************************/


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
                if (! Character.isLetter(text.charAt(0)))
                {
                    text = "op";
                }
                System.out.println("<"+ text + ">");

                // print children
                printXml((TokenAST) node.getFirstChild());

                // print end tag
                System.out.println("</"+ node.getText() + ">");
            }
        }
    }


    public static void main(String[] args)
    {
        try
        {
            java.io.OutputStream os;

            DataScriptXmlDumper dsTool = new DataScriptXmlDumper();
            dsTool.context = ToolContext.getInstance();
            int i = 0;
            //dsTool.context.setPathName(args[i++]);
            dsTool.context.setFileName(args[i++]);
            AST unitRoot = (TokenAST) dsTool.parsePackage();
            dsTool.rootNode.addChild(unitRoot);
            dsTool.parseImportedPackages(unitRoot);

            os = System.out;    // or a file output stream

            /*
            OutputStreamWriter osw = new OutputStreamWriter(System.out);
            dsTool.rootNode.xmlSerialize(osw);
            os.flush();
            printXml(dsTool.rootNode);
            System.out.println(dsTool.rootNode.toStringList());
            //*/

            ASTFrame frame = new ASTFrame("AST", dsTool.rootNode);
            frame.setVisible(true);
            
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new SAXSource(dsTool, new InputSource());
            Result result = new StreamResult(new OutputStreamWriter(os));
            t.transform(source, result);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
