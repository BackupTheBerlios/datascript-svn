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
import java.io.FileNotFoundException;

import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.debug.misc.ASTFrame;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;

public class CommentParserTest 
{
    private CommonAST rootNode;
    private DocCommentParser parser;
    
    public void parseComment(String fileName)
        throws FileNotFoundException, RecognitionException, TokenStreamException
    {
        FileInputStream is = new FileInputStream(fileName); 
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer buffer = new TokenBuffer(lexer);
        parser = new DocCommentParser(buffer);
        parser.comment();
        rootNode = (CommonAST) parser.getAST(); //.getFirstChild();
        rootNode.setText("root");
    }

    public void printTokens(String fileName) throws Exception
    {
        FileInputStream is = new FileInputStream(fileName); 
        DocCommentLexer lexer = new DocCommentLexer(is);
        TokenBuffer buffer = new TokenBuffer(lexer);
        while (buffer.LA(1) != Token.EOF_TYPE)
        {
            Token token = buffer.LT(1);
            System.out.println(DocCommentParser._tokenNames[token.getType()] + 
                    ": " + token.getText());
            buffer.consume();
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException,
            RecognitionException, TokenStreamException
    {
        String fileName = args[0];
        CommentParserTest self = new CommentParserTest();
        self.parseComment(fileName);
        // self.printTokens(fileName);
        // OutputStreamWriter os = new OutputStreamWriter(System.out);
        // root.xmlSerialize(os);
        // os.flush();
        // printXml(dsTool.rootNode);
        // System.out.println(dsTool.rootNode.toStringList());
        ASTFrame frame = new ASTFrame("AST", self.rootNode);
        frame.setVisible(true);

    }
}
