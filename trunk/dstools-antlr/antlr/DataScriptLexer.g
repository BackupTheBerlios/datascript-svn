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
import datascript.tools.ToolContext;
}

class DataScriptLexer extends Lexer;

options 
{
	k=3;                   // 3 characters of lookahead
	charVocabulary='\u0003'..'\u7FFE';
	importVocab=DataScriptParser;
	// without inlining some bitset tests, couldn't do unicode;
	// I need to make ANTLR generate smaller bitsets; see
	// bottom of JavaLexer.java
	codeGenBitsetTestThreshold=20;
        testLiterals=false;
}

tokens
{
    NUM_LONG;
    NUM_FLOAT;
    NUM_DOUBLE;
    NUM_BINARY;
}


{
    private ToolContext context;
    
    public void setToolContext(ToolContext context)
    {
        this.context = context;
    }
}




// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
ID options {testLiterals=true;}
	:	('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
	;






// OPERATORS

QUESTIONMARK		:	'?'	;
LPAREN			:	'('     ;
RPAREN			:	')'	;
LBRACKET		:	'['	;
RBRACKET		:	']'	;
LCURLY			:	'{'	;
RCURLY			:	'}'	;
COLON			:	':'	;
DOUBLECOLON		:	"::"	;
COMMA			:	','	;
DOT			:	'.'	;
ASSIGN			:	'='	;
BANG			:	'!'	;
DIVIDE			:	'/'	;
DIVASSIGN		:	"/="	;
PLUS			:	'+'	;
PLUSASSIGN		:	"+="	;
MINUS			:	'-'	;
MINUSASSIGN             :	"-="	;
MULTIPLY		:	'*'	;
MULTASSIGN		:	"*="	;
MODULO			:	'%'	;
MODASSIGN		:	"%="	;
RSHIFT			:	">>"	;
RSHIFTASSIGN		:	">>="	;
LSHIFT			:	"<<"	;
LSHIFTASSIGN            :	"<<="	;
XORASSIGN		:	"^="	;
ORASSIGN		:	"|="	;
LOGICALOR		:	"||"	;
TILDE			:	'~'	;
AND			:	'&'	;
OR			:	'|'	;
XOR			:	'^'	;
ANDASSIGN		:	"&="	;
LOGICALAND		:	"&&"	;
SEMICOLON		:	';'	;
RANGE                   :       ".."    ;
EQ                      :       "=="    ;
NE                      :       "!="    ;
LT                      :       "<"     ;
LE                      :       "<="    ;
GT                      :       ">"     ;
GE                      :       ">="    ;

// Whitespace -- ignored
WS	:	(	' '
		|	'\t'
		|	'\f'
			// handle newlines
		|	(	options {generateAmbigWarnings=false;}
			:	"\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
			)
			{ newline(); }
		)+
		{ _ttype = Token.SKIP; }
	;

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)?
		{$setType(Token.SKIP); newline();}
	;


COMMENT
    : "/*" 
      ( "*/" {$setType(Token.SKIP);}
      | {LA(2) != '/'}? "*" PLAIN_COMMENT_CONTENT "*/" {$setType(DOC);}
      | ~'*' PLAIN_COMMENT_CONTENT "*/" {$setType(Token.SKIP);}
      )
		
    ;
    
protected
PLAIN_COMMENT_CONTENT
    :           
		( options { generateAmbigWarnings=false;} :	{ LA(2)!='/' }? '*'
		|	'\r'! '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		{$setType(Token.SKIP);}
	;

protected
DOC_CONTENT
    :           
		( options { generateAmbigWarnings=false;} :	{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
	;




// character literals
CHAR_LITERAL
	:	'\'' ( ESC | ~('\''|'\n'|'\r'|'\\') ) '\''
	;

// string literals
STRING_LITERAL
	:	'"' (ESC|~('"'|'\\'|'\n'|'\r'))* '"'
	;


// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)?
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
			)?
		)
	;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;


// a numeric literal
INTEGER_LITERAL
	{boolean isDecimal=false; Token t=null;}
    :   ( ('0'..'1')+ ('b'|'B') ) =>   ('0'..'1')+ ('b'|'B')
        | 
    	(	'0' 
			(	('x'|'X') // hex
				(			
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options {
						warnWhenFollowAmbig=false;
					}
				:	HEX_DIGIT
				)+

			|	('0'..'7')+	// octal
			)?
		|	('1'..'9') ('0'..'9')*  // non-zero decimal
		)
		(	('l'|'L') { _ttype = NUM_LONG; }

        )?
	;


