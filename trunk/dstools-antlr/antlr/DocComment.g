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
    : "/**"^ (NEWLINE!)* commentBody filler EOC! EOF!
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
