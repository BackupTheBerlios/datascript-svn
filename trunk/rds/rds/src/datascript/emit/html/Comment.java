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
package datascript.emit.html;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import antlr.ANTLRException;
import antlr.TokenBuffer;
import antlr.collections.AST;
import datascript.antlr.DocCommentLexer;
import datascript.antlr.DocCommentParser;
import datascript.antlr.DocCommentParserTokenTypes;


/**
 * @author HWellmann
 * 
 */
public class Comment
{
	private List<String> paragraphs = new ArrayList<String>();
	private List<Tag> tags = new ArrayList<Tag>();
	
	public static class Tag
	{
		private String name;
		private String head;
		private String tail;
		
		public Tag(String name, String head, String tail)
		{
			this.name = name;
			this.head = head;
			this.tail = tail;
		}
		
		public String getName()
		{
			return name;
		}

		public String getHead()
		{
			return head;
		}
		public String getTail()
		{
			return tail;
		}
	}
	
	public void parse(String doc)
	{
		try
		{
			StringReader is = new StringReader(doc);
			DocCommentLexer lexer = new DocCommentLexer(is);
			TokenBuffer tBuffer = new TokenBuffer(lexer);
			DocCommentParser parser = new DocCommentParser(tBuffer);

			parser.comment();
			AST docNode = parser.getAST();
			AST child = docNode.getFirstChild();
			
	        for ( ; child != null && child.getType() == DocCommentParserTokenTypes.TEXT; 
	        	child = child.getNextSibling())
	        {
	        	StringBuilder buffer = new StringBuilder(child.getText());
	        	AST textAst = child.getFirstChild();

		        for (; textAst != null; textAst = textAst.getNextSibling())
		        {
	    			buffer.append(textAst.getText());
		        }
		        String para = buffer.toString();
		        paragraphs.add(para);
	        }	        	

	        for ( ; child != null && child.getType() == DocCommentParserTokenTypes.AT; 
	        	child = child.getNextSibling())
	        {
	        	StringBuilder buffer = new StringBuilder();
                String tagName = child.getText();
	        	AST textAst = child.getFirstChild();
                for (; textAst != null; textAst = textAst.getNextSibling())
		        {
	    			buffer.append(textAst.getText());
		        }
                String text = buffer.toString();
                String[] parts = text.split("\\s+", 2);
                String head = parts[0];
                String tail = (parts.length == 2) ? parts[1] : "";
                Tag tag = new Tag(tagName, head, tail);
                tags.add(tag);
	        }	        
		}
		catch (ANTLRException exc)
		{
			exc.printStackTrace();
		}
		
	}
	
	public List<String> getParagraphs()
	{
		return paragraphs;
	}
	
	public List<Tag> getTags()
	{
		return tags;
	}
}
