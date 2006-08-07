class DataScriptParser extends Parser;

options
{
    k=2;
    buildAST=true;
}

tokens
{
    FIELD;
    STRUCT;
    UNION;
    PARAM;
    INST;
    ARRAY;
    LABEL;
    BLOCK;
    CAST;
    MEMBERS;
    TYPEREF;
    UPLUS;
    UMINUS;
}

translationUnit
    :   declarationList EOF!
    ;    

declarationList
    :   (declaration)*
        { #declarationList = #([MEMBERS], declarationList); }
    ;


declaration
    :   fieldDefinition SEMICOLON!
    |   conditionDefinition
    |   constDeclaration  SEMICOLON!
    ;


label!
    :   ( globalLabel ) => g:globalLabel e:expression COLON
        { #label = #([LABEL, "label"], e, g); }
    |   x:expression COLON
        { #label = #([LABEL, "label"], x); }
    ;

globalLabel
    :   expression DOUBLECOLON!
    ;

/******************* begin of condition stuff *****************/


conditionDefinition
    :   "condition"^ ID parameterList conditionBlock
    ;


parameterList 
    :   LPAREN! (parameterDefinition ( COMMA! parameterDefinition )* )? RPAREN!
        { #parameterList = #([PARAM, "param"], #parameterList); }
    ;

conditionBlock!
    :   LCURLY^ (e:conditionExpression SEMICOLON!)* RCURLY!
        { #conditionBlock = #([BLOCK, "BLOCK"], e); }
    ;

parameterDefinition
    : typeDeclaration ID
    ;


conditionExpression
    : expression
    ;


/******************* begin of enumerator stuff *****************/

enumDeclaration
    : "enum"^ builtinType (ID)? enumMemberList
    ;

enumMemberList
    : LCURLY! enumItem (COMMA! enumItem)* RCURLY!
      { #enumMemberList = #([MEMBERS], enumMemberList); }
    ;

enumItem
    : (DOC)? ID ( ASSIGN! constantExpression )?
      { #enumItem = #([FIELD], enumItem); }
    ;

bitmaskDeclaration
    : "bitmask"^  builtinType (ID)? enumMemberList
    ;

/******************* end of enumerator stuff *****************/


constDeclaration
    :   "const"^ typeDeclaration ID ASSIGN! typeValue
    ;


typeValue
    :   constantExpression
    |   LCURLY^ typeValueList RCURLY!
    ;

typeValueList
    :   typeValue (COMMA! typeValue)*
    ;


/****************************************************************/

fieldDefinition!
    :   (d:DOC)?
        ( (label) => l:label )?
        t:typeReference
        (f:ID)? 
        (a:arrayRange {#t = #([ARRAY, "ARRAY"], t, a); } )?
        (i:fieldInitializer)? 
        (o:fieldOptionalClause)?
        (c:fieldCondition)?
        { #fieldDefinition = #([FIELD, "field"], t, f, i, o, c, d, l); }
    ;

typeArgumentList
    :   LPAREN! (functionArgument (COMMA! functionArgument)* )? RPAREN!
    ;


fieldInitializer
    :   ASSIGN^ typeValue
    ;

fieldOptionalClause
    :   "if"^ expression
    ;

fieldCondition
    :   COLON^ expression
    ;

typeDeclaration
    :   ( (byteOrderModifier)? ("union")? (ID)? (LPAREN parameterDefinition | LCURLY)) =>
        structDeclaration
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    ;

typeReference
    :   ( (byteOrderModifier)? ("union")? (ID)? (LPAREN parameterDefinition | LCURLY)) =>
        structDeclaration
    |   (ID LPAREN) => paramTypeInstantiation
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    ;

paramTypeInstantiation
    :   definedType typeArgumentList
        { #paramTypeInstantiation = #([INST, "INST"], paramTypeInstantiation); }
    ;
structDeclaration!
    :   (byteOrderModifier)? 
        (u:"union")? 
        (n:ID)?
        (p:parameterList)? m:memberList
        { if (u == null)
          {
              #structDeclaration = #([STRUCT, "struct"], n, p, m); 
          }
          else
          {
              #structDeclaration = #([UNION, "union"], n, p, m); 
          }
        } 
    ;

memberList
    :   LCURLY! (declaration)* RCURLY!
        { #memberList = #([MEMBERS], #memberList); }
    ;

definedType
    :   typeSymbol    {#definedType = #([TYPEREF], #definedType);}
    |   builtinType
    ;

typeSymbol
    :   ID (dotOperand)*
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    :   "uint8"
    |   "uint16"
    |   "uint32"
    |   "uint64"
    |   "int8"
    |   "int16"
    |   "int32"
    |   "int64"
    |   "string"
    |   bitField
    ;

bitField
    :   "bit"^ (  COLON! INTEGER_LITERAL
               | LT! e:shiftExpression GT!  
               )
    ;


byteOrderModifier
    :   "big"
    |   "little"
    ;

arrayRange
    :   LBRACKET! (r:rangeExpression)? RBRACKET!
    ;



/*********************************************************************/


expression
    :   assignmentExpression (COMMA^ assignmentExpression)*
    ;

assignmentExpression // options { k=3;}
    :!  (unaryExpression assignmentOperator) => l:unaryExpression op:assignmentOperator r:assignmentExpression
                                                { #assignmentExpression = #(op, l, r); }
    |   quantifiedExpression
    ;


assignmentOperator
    :   ASSIGN
    |   MULTASSIGN
    |   DIVASSIGN
    |   MODASSIGN
    |   PLUSASSIGN
    |   MINUSASSIGN
    |   LSHIFTASSIGN
    |   RSHIFTASSIGN
    |   ANDASSIGN
    |   XORASSIGN
    |   ORASSIGN
    ;

quantifiedExpression
    :   (quantifier)? conditionalExpression
    ;

quantifier
    :   "forall"^ ID "in"! unaryExpression COLON!
    ;

conditionalExpression
    :   logicalOrExpression (QUESTIONMARK^ expression COLON! conditionalExpression)?
    ;


constantExpression
    :   conditionalExpression
    ;

rangeExpression
    :   expression (RANGE! expression)?
    ;

logicalOrExpression
    :   logicalAndExpression (LOGICALOR^ logicalOrExpression)?
    ;

logicalAndExpression
    :   inclusiveOrExpression  (LOGICALAND^ logicalAndExpression)?
    ;

inclusiveOrExpression
    :   exclusiveOrExpression (OR^ inclusiveOrExpression)?
    ;

exclusiveOrExpression
    :   andExpression (XOR^ exclusiveOrExpression)?
    ;

andExpression
    :   equalityExpression (AND^ andExpression)?
    ;

equalityExpression
    :   relationalExpression  (( EQ^ | NE^ ) equalityExpression)?
    ;

relationalExpression
    :   shiftExpression (( LT^ | LE^ | GT^ | GE^) shiftExpression)*
    ;

shiftExpression
    :   additiveExpression ((LSHIFT^ | RSHIFT^) additiveExpression)*
    ;

additiveExpression
    :   multiplicativeExpression  ((PLUS^ | MINUS^) multiplicativeExpression)*
    ;

multiplicativeExpression
    :   castExpression ((MULTIPLY^ | DIVIDE^ | MODULO^) castExpression)*
    ;

castExpression
    :   (castOperand) => castOperand
    |   unaryExpression
    ;

castOperand
    :   LPAREN! definedType RPAREN! castExpression
        { #castOperand = #([CAST, "CAST"], #castOperand); }
    ;

unaryExpression
    :   postfixExpression
    |   unaryOperand
    |   sizeOfOperand
    |   lengthOfOperand
    ;

unaryOperand!
    :   o:unaryOperator e:castExpression { #unaryOperand = #(o, e); }
    ;

unaryOperator
    :   PLUS!   {#unaryOperator = #[UPLUS]; }
    |   MINUS!  {#unaryOperator = #[UMINUS]; }
    |   TILDE 
    |   BANG
    ;

sizeOfOperand
    :   "sizeof"^ unaryExpression
    ;

lengthOfOperand
    :   "lengthof"^ unaryExpression
    ;

postfixExpression!
    :   e:primaryExpression  { #postfixExpression = #e; }
        (o:postfixOperand 
                          { AST rhs = #o.getFirstChild(); 
                            #postfixExpression= #(o, postfixExpression, rhs); }
        )*
    ;
    
postfixOperand
    :    
        arrayOperand | functionArgumentList | dotOperand | choiceOperand
    ;

choiceOperand
    :   "is"^ ID
    ;

arrayOperand!
    :   LBRACKET e:expression RBRACKET
        { #arrayOperand = #([ARRAY, "ARRAY"], e); }
    ;

functionArgumentList
    :   LPAREN! (functionArgument (COMMA! functionArgument)* )? RPAREN!
        { #functionArgumentList = #([INST, "INST"], functionArgumentList); }
    ;

dotOperand
    :   DOT^ ID
    ;


primaryExpression
    :   variableName
    |   constant
    |   parenthesizedExpression
    ;

parenthesizedExpression
    :   LPAREN^ expression RPAREN!
    ;

variableName
    :   ID
    ;

functionArgument
    :   assignmentExpression
    ;

constant
    :   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;



class DataScriptLexer extends Lexer;


options 
{
	k=3;                   // 3 characters of lookahead
	charVocabulary='\u0003'..'\u7FFE';
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


// -------------------------------------------------------------------------

class DataScriptWalker extends TreeParser;

{
    public void reportError(RecognitionException ex) {
        System.out.println(ex.toString());
        throw new RuntimeException(ex);
    }
}

translationUnit
    :   #(MEMBERS (declaration)*)
    ;    


declaration
    :   fieldDefinition 
    //|   conditionDefinition
    |   constDeclaration 
    ;


label
    :   #(LABEL expression (expression)?)
    ;

/*


conditionDefinition
    :   "condition"^ ID parameterList conditionBlock
    ;

conditionBlock!
    :   LCURLY^ (e:conditionExpression SEMICOLON!)* RCURLY!
        { #conditionBlock = #([BLOCK, "BLOCK"], e); }
    ;


conditionExpression
    : expression
    ;
*/

parameterList 
    :   #(PARAM (typeDeclaration ID)*)
    ;



/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #("enum" builtinType (ID)? enumMemberList)
    ;

enumMemberList
    : #(MEMBERS (enumItem)+)
    ;

enumItem
    : #(FIELD (DOC)? ID (expression)?)
    ;

bitmaskDeclaration
    : #("bitmask"  builtinType (ID)? enumMemberList)
    ;

constDeclaration
    : #("const" typeReference ID expression)
    ;

fieldDefinition
    :   #(FIELD typeReference (ID)? 
          (fieldInitializer)?
          (fieldOptionalClause)?
          (fieldCondition)? (DOC)? (label)?
          ) 
    ;

typeArgumentList
    :   (expression)+
    ;


fieldInitializer
    :   #(ASSIGN typeValue)
    ;

fieldOptionalClause
    :   #("if" expression)
    ;

fieldCondition
    :   #(COLON expression)
    ;

typeDeclaration
    :   structDeclaration
    |   unionDeclaration
    |   enumDeclaration
    |   bitmaskDeclaration
    |   arrayType
    ;

typeReference
    :   structDeclaration
    |   unionDeclaration
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    |   arrayType
    |   paramTypeInstantiation
    ;

arrayType
    :  #(ARRAY typeReference arrayRange)
    ;

paramTypeInstantiation
    : #(INST definedType typeArgumentList)
    ;
    
structDeclaration
    :   #(STRUCT (ID)? (parameterList)? memberList)
    ;

unionDeclaration
    :   #(UNION (ID)? (parameterList)? memberList)
    ;

memberList
    :    #(MEMBERS (declaration)*)
    ;

definedType
    :  #(TYPEREF ID (DOT ID)*) 
    |   builtinType
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    :   "uint8"
    |   "uint16"
    |   "uint32"
    |   "uint64"
    |   "int8"
    |   "int16"
    |   "int32"
    |   "int64"
    |   "string"
    |   bitField
    ;

bitField
    :   #("bit" expression)
    ;


byteOrderModifier
    :   "big"
    |   "little"
    ;

arrayRange
    :   (expression (expression)?)?
    ;

typeValue
    :   expression
    |   #(LCURLY (typeValue)+)
    ;



// ------- expressions ----------------------------------------------------

expression
    :   #(COMMA expression expression)
    |   #(ASSIGN expression expression)
    |   #(MULTASSIGN expression expression)
    |   #(DIVASSIGN expression expression)
    |   #(MODASSIGN expression expression)
    |   #(PLUSASSIGN expression expression)
    |   #(MINUSASSIGN expression expression)
    |   #(LSHIFTASSIGN expression expression)
    |   #(RSHIFTASSIGN expression expression)
    |   #(ANDASSIGN expression expression)
    |   #(XORASSIGN expression expression)
    |   #(ORASSIGN expression expression)
    |   #("forall" expression expression expression)
    |   #(QUESTIONMARK expression expression expression)
    |   #(LOGICALOR expression expression)
    |   #(LOGICALAND expression expression)
    |   #(OR expression expression)
    |   #(XOR expression expression)
    |   #(AND expression expression)
    |   #(EQ expression expression)
    |   #(NE expression expression)
    |   #(LT expression expression)
    |   #(GT expression expression)
    |   #(LE expression expression)
    |   #(GE expression expression)
    |   #(LSHIFT expression expression)
    |   #(RSHIFT expression expression)
    |   #(PLUS expression expression)
    |   #(MINUS expression expression)
    |   #(MULTIPLY expression expression)
    |   #(DIVIDE expression expression)
    |   #(MODULO expression expression)
    |   #(CAST definedType expression)
    |   #(UPLUS expression)
    |   #(UMINUS expression)
    |   #(TILDE expression)
    |   #(BANG expression)
    |   #("sizeof" expression)
    |   #("lengthof" expression)
    |   #(DOT expression)
    |   #(ARRAY expression)
    |   #(INST (expression)+)
    |   #(LPAREN expression)
    |   #("is" ID)
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;


