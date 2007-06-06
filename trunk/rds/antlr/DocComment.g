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
header
{
package datascript.antlr;
}

class DocCommentParser extends Parser;

options
{
    k=3;
    buildAST=true;
}

tokens
{
    FIELD;
}


comment
    : "/**"^ (NEWLINE!)* (commentBody filler)? EOC! EOF!
    ;
    
strippedComment
    : (NEWLINE)* commentBody EOF
    ;
    
commentBody
    : commentText (((NEWLINE)+ AT) =>  (NEWLINE!)+ tags)?
    | tags
    ;
   
commentText

    : commentParagraph (((NEWLINE)+ TEXT) => NEWLINE! (NEWLINE!)+ commentText)?
    ;
    
commentParagraph

    : TEXT^ (NEWLINE TEXT)*
    ;    
   

    

tags
    :  tag (((NEWLINE)+ AT) => (NEWLINE!)+ tags)? 
    ;
    
filler 
    : (NEWLINE) => NEWLINE filler
    |
    ;    
    
tag	
    : AT^  TEXT (NEWLINE TEXT)* 
    ;


class DocCommentLexer extends Lexer;


options 
{
	k=4;                   // 3 characters of lookahead
	charVocabulary='\u0003'..'\u7FFE';
}


   
NEWLINE 
    : NL (PREFIX!)?
    ;
 

protected
PREFIX!
    : WS ({LA(2) != '/'}? (STAR)+ (WS)?)?
    | (STAR)+ (WS)?
    ;

protected
WS!
    : (' '
    | '\t'
    | '\f')+
    ;
    
protected
STAR! : {LA(2) != '/'}? "*" ;    

EOC : "*/" ;

protected
NL options { generateAmbigWarnings=false;}
    : '\r'! '\n'		{newline();}
    | '\r'			{newline();}
    | '\n'			{newline();}
    ;
    
TEXT
    : //(PREFIX!)? 
      ( AT     {$setType(AT); }
      | {!(LA(1)=='/' && LA(2) == '*')}? ~('\r'|'\n'|'@'|' '|'\t'|'\f'|'*') 
         (  ~('\r'|'\n'|'*')
         | {LA(2) != '/'}? '*'
         )+
      | "/**" (PREFIX)?
      )
    ;    

protected
AT
    : "@"! TAGNAME WS
    ;

protected
TAGNAME
    : "param"
    | "see"
    | "todo"
    ;
