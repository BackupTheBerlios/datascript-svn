/* BSD License
 *
 * Copyright (c) 2006, Henrik Wedekind, Harman/Becker Automotive Systems
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
package datascript.emit.xml;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.Option;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import antlr.ANTLRException;
import antlr.TokenBuffer;
import antlr.collections.AST;
import datascript.antlr.DataScriptEmitter;
import datascript.antlr.DataScriptParser;
import datascript.antlr.DataScriptParserTokenTypes;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;
import datascript.antlr.DocCommentParserTokenTypes;
import datascript.antlr.util.TokenAST;
import datascript.ast.DataScriptException;
import datascript.tools.Extension;
import datascript.tools.Parameters;


public class XmlExtension extends XMLFilterImpl implements Extension
{
    private Parameters params = null;

    private TokenAST rootNode;
    private ContentHandler handler;
    private AttributesImpl noAttr = new AttributesImpl();


    /* (non-Javadoc)
     * @see datascript.tools.Extension#generate(datascript.antlr.DataScriptEmitter, datascript.ast.TokenAST)
     */
    public void generate(DataScriptEmitter emitter, TokenAST root)
    {
        if (params == null)
            throw new DataScriptException("No parameters set for XmlBackend!");

        if (!params.argumentExists("-xml"))
        {
            System.out.println("emitting XML file is disabled.");
            return;
        }

        System.out.println("emitting xml");
        
        String fileName = params.getCommandLineArg("-xml");
        if (fileName == null)
        {
            fileName = "datascript.xml";
        }
        File outputFile = new File(params.getOutPathName(), fileName);
        this.rootNode = root;
        FileOutputStream os;
        try
        {
            os = new FileOutputStream(outputFile);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setAttribute("indent-number", new Integer(2));
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            Source source = new SAXSource(this, new InputSource());
            Result result = new StreamResult(new OutputStreamWriter(os));
            t.transform(source, result);
        }
        catch (FileNotFoundException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TransformerConfigurationException exc)
        {
            throw new DataScriptException(exc);
        }
        catch (TransformerException exc)
        {
            throw new DataScriptException(exc);
        }
    }


    public void getOptions(org.apache.commons.cli.Options rdsOptions)
    {
        Option rdsOption;

        rdsOption = new Option("xml", false, 
                "enables generation of a XML file of the syntaxtree");
        rdsOption.setRequired(false);
        rdsOptions.addOption(rdsOption);
    }


    /* (non-Javadoc)
     * @see datascript.tools.Extension#setParameter(datascript.tools.Parameters)
     */
    public void setParameters(Parameters params)
    {
        this.params = params;
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
        DataScriptParser parser = params.getParser();
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
    @Override
    public void parse(InputSource is) throws SAXException
    {
        handler = getContentHandler();
        handler.startDocument();
        fireSaxEvents(rootNode);
        handler.endDocument();
    }

}
