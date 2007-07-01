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

import datascript.ast.*;
import datascript.antlr.util.*;
}

class DataScriptParser extends Parser;

options
{
    k=2;
    buildAST=true;
}

// Keep the token list sorted alphabetically.
tokens
{    
    AND<AST=datascript.ast.IntegerExpression>;
    ARRAY<AST=datascript.ast.ArrayType>;
    ARRAYELEM<AST=datascript.ast.Expression>;
    BANG<AST=datascript.ast.BooleanExpression>;
    BIG="big";
    BIT<AST=datascript.ast.BitFieldType>;
    BLOCK;
    CAST;
    COMMENT;
    CONST="const"<AST=datascript.ast.ConstType>;
    DIVIDE<AST=datascript.ast.IntegerExpression>;
    DOC;
    DOT<AST=datascript.ast.Expression>;
    ENUM="enum"<AST=datascript.ast.EnumType>;
    EQ<AST=datascript.ast.BooleanExpression>;
    FIELD<AST=datascript.ast.Field>;
    FUNCTION="function"<AST=datascript.ast.FunctionType>;
    FUNCTIONCALL<AST=datascript.ast.Expression>;
    FUNCTIONS;
    GE<AST=datascript.ast.BooleanExpression>;
    GT<AST=datascript.ast.BooleanExpression>;
    IF="if";
    IMPORT="import";
    INDEX<AST=datascript.ast.Expression>;
    INST<AST=datascript.ast.TypeInstantiation>;
    INT16="int16"<AST=datascript.ast.StdIntegerType>;
    INT32="int32"<AST=datascript.ast.StdIntegerType>;
    INT64="int64"<AST=datascript.ast.StdIntegerType>;
    INT8="int8"<AST=datascript.ast.StdIntegerType>;
    INTEGER_LITERAL<AST=datascript.ast.IntegerExpression>;
    ITEM<AST=datascript.ast.EnumItem>;
    LABEL;
    LE<AST=datascript.ast.BooleanExpression>;
    LENGTHOF<AST=datascript.ast.IntegerExpression>;
    LITTLE="little";
    LOGICALAND<AST=datascript.ast.BooleanExpression>;
    LOGICALOR<AST=datascript.ast.BooleanExpression>;
    LPAREN<AST=datascript.ast.Expression>;
    LSHIFT<AST=datascript.ast.IntegerExpression>;
    LT<AST=datascript.ast.BooleanExpression>;
    MEMBERS;
    MINUS<AST=datascript.ast.IntegerExpression>;
    MODULO<AST=datascript.ast.IntegerExpression>;
    MULTIPLY<AST=datascript.ast.IntegerExpression>;
    NE<AST=datascript.ast.BooleanExpression>;
    OR<AST=datascript.ast.IntegerExpression>;
    PACKAGE="package";
    PARAM;
    PARAMLIST;
    PLUS<AST=datascript.ast.IntegerExpression>;
    QUESTIONMARK<AST=datascript.ast.Expression>;
    RETURN="return";
    ROOT;
    RSHIFT<AST=datascript.ast.IntegerExpression>;
    SEQUENCE<AST=datascript.ast.SequenceType>;
    SIZEOF<AST=datascript.ast.IntegerExpression>;
    SQL;
    SQL_DATABASE<AST=datascript.ast.SqlDatabaseType>;
    SQL_INTEGER<AST=datascript.ast.SqlIntegerType>;
    SQL_METADATA<AST=datascript.ast.SqlMetadataType>;
    SQL_PRAGMA<AST=datascript.ast.SqlPragmaType>;
    SQL_TABLE<AST=datascript.ast.SqlTableType>;
    STRING<AST=datascript.ast.StringType>;
    SUBTYPE<AST=datascript.ast.Subtype>;
    SUM<AST=datascript.ast.Expression>;
    TILDE<AST=datascript.ast.IntegerExpression>;
    TRANSLATION_UNIT;
    TYPEREF<AST=datascript.ast.TypeReference>;
    UINT16="uint16"<AST=datascript.ast.StdIntegerType>;
    UINT32="uint32"<AST=datascript.ast.StdIntegerType>;
    UINT64="uint64"<AST=datascript.ast.StdIntegerType>;
    UINT8="uint8"<AST=datascript.ast.StdIntegerType>;
    UMINUS<AST=datascript.ast.IntegerExpression>;
    UNION<AST=datascript.ast.UnionType>;
    UPLUS<AST=datascript.ast.IntegerExpression>;
    WS;
    XOR<AST=datascript.ast.IntegerExpression>;
    ZIP="zip";
}

{
    private ToolContext context;
    
    public void setContext(ToolContext context)
    {
        this.context = context;
    }
}


translationUnit
    : (packageDeclaration)? (importDeclaration)* declarationList EOF!
      { #translationUnit = #([TRANSLATION_UNIT, "TRANSLATION_UNIT"], translationUnit); }
    ;    

packageDeclaration
    : PACKAGE^ ID (DOT! ID)* SEMICOLON!
    ;
    
importDeclaration
    : IMPORT^ ID (DOT! ID)* DOT! MULTIPLY! SEMICOLON!
    ;   

declarationList
    :   ((d:DOC!)? declaration[#d] {#d=null;} )*
        { #declarationList = #([MEMBERS, "MEMBERS"], declarationList); }
    ;


declaration[AST doc]
    :   fieldDefinition[doc] SEMICOLON!
    |   conditionDefinition
    |   constDeclaration  SEMICOLON!
    |   subtypeDeclaration[doc] SEMICOLON!
    |   sqlDatabaseDefinition[doc]  SEMICOLON!
    |   sqlTableDeclaration  SEMICOLON!
    |   sqlIntegerDeclaration  SEMICOLON!
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
        { #parameterList = #([PARAMLIST, "paramlist"], #parameterList); }
    ;

conditionBlock!
    :   LCURLY^ (e:conditionExpression SEMICOLON!)* RCURLY!
        { #conditionBlock = #([BLOCK, "BLOCK"], e); }
    ;

parameterDefinition
    : definedType ID
      { #parameterDefinition = #([PARAM, "param"], #parameterDefinition); }
    ;


conditionExpression
    : expression
    ;


/******************* begin of enumerator stuff *****************/

enumDeclaration
    : ENUM^ builtinType (ID)? enumMemberList
    ;

enumMemberList
    : LCURLY! enumItem (COMMA! enumItem)* RCURLY!
      { #enumMemberList = #([MEMBERS, "ENUM_MEMBER"], enumMemberList); }
    ;

enumItem
    : (DOC)? ID ( ASSIGN! constantExpression )?
      { #enumItem = #([ITEM], enumItem); }
    ;

bitmaskDeclaration
    : "bitmask"^  builtinType (ID)? enumMemberList
    ;

/******************* end of enumerator stuff *****************/


constDeclaration
    : CONST^ builtinType ID ASSIGN! typeValue
    ;


typeValue
    :   constantExpression
    |   LCURLY^ typeValueList RCURLY!
    ;

typeValueList
    :   typeValue (COMMA! typeValue)*
    ;


/****************************************************************/

fieldDefinition![AST d]
    :   
        ( (label) => l:label )?
        ( (zipModifier typeReference ID arrayRange) => z:zipModifier )?
        t:typeReference
        (f:ID)? 
        (a:arrayRange {#t = #([ARRAY], t, a); } )?
        (i:fieldInitializer)? 
        (o:fieldOptionalClause)?
        (c:fieldCondition)?
        { 
            #fieldDefinition = #([FIELD], t, f, i, o, c, d, l, z); 
            AST doc = #t;
            if (#l != null)
            {
                doc = #l;
            }
            else if (#z != null)
            {
                doc = #z;
            }
            else if (#t.getType() == TYPEREF)
            {
                doc = #t.getFirstChild();
            }
            Token docToken = null;
            if (doc != null)
            {
                docToken = ((TokenAST)doc).getHiddenBefore();
            }
            Field field = (Field) #fieldDefinition;
            field.setDocumentation(docToken);
        }
    ;

typeArgumentList
    :   LPAREN! (functionArgument (COMMA! functionArgument)* )? RPAREN!
    ;


fieldInitializer
    :   ASSIGN^ typeValue
    ;

fieldOptionalClause
    :   IF^ expression
    ;

fieldCondition
    :   COLON^ expression
    ;

typeDeclaration
    :   ( (modifier)* ("union")? (ID)? (LPAREN parameterDefinition | LCURLY)) =>
        sequenceDeclaration
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    |   sqlIntegerDeclaration
    |   sqlTableDeclaration
    ;

typeReference
    :   ( (modifier)* ("union")? (ID)? (LPAREN parameterDefinition | LCURLY)) =>
        sequenceDeclaration
    |   (ID LPAREN) => paramTypeInstantiation
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    ;

paramTypeInstantiation
    :   definedType typeArgumentList
        { #paramTypeInstantiation = #([INST], paramTypeInstantiation); }
    ;

sequenceDeclaration!
    :   (modifier)* 
        (u:"union")? 
        (n:ID)?
        (p:parameterList)? 
        m:memberList        
        { if (u == null)
          {
              #sequenceDeclaration = #([SEQUENCE], n, p, m);
              SequenceType s = (SequenceType) #sequenceDeclaration;
              FileNameToken id = (FileNameToken) n;
              Token t = id.getHiddenBefore();
              s.setDocumentation(t);              
          }
          else
          {
              #sequenceDeclaration = #([UNION], n, p, m); 
              UnionType s = (UnionType) #sequenceDeclaration;
              FileNameToken first = (FileNameToken) u;
              Token t = first.getHiddenBefore();
              s.setDocumentation(t);              
          }
        } 
    ;

memberList
    :   LCURLY! declarationList (f:functionList)? RCURLY!
    ;
    
functionList
    : functionDefinition (SEMICOLON! functionDefinition)*
      { #functionList = #([FUNCTIONS, "FUNCTIONS"], #functionList); }
    ;
    
functionDefinition!
    : "function"! t:integerType n:ID functionParamList b:functionBody
      { #functionDefinition = #([FUNCTION, "FUNCTION"], n, t, b); }
    ;
    
functionParamList
    : LPAREN! RPAREN!
    ;
    
functionBody
    : LCURLY! "return"! expression SEMICOLON! RCURLY!
      { #functionBody = #([RETURN], #functionBody); }
    ;    
            

definedType
    :   typeSymbol    {#definedType = #([TYPEREF], #definedType);}
    |   builtinType
    ;


typeSymbol
    :   ID (dotOperand)*
    ;

// TODO: definedType includes typeSymbols with dotOperands
// Is this really what we want?

subtypeDeclaration[AST doc]
    :   "subtype"! definedType ID (c:fieldCondition)?
        { #subtypeDeclaration = #([SUBTYPE], #subtypeDeclaration, doc, c); }
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

integerType
    :   UINT8
    |   UINT16
    |   UINT32
    |   UINT64
    |   INT8
    |   INT16
    |   INT32
    |   INT64
    ;

stringType
    :	"string"!     {#stringType = #[STRING];}
    ;

builtinTypeDefaultOrder
    :   integerType
    |   stringType
    |   bitField
    ;

bitField
    : "bit"! (  COLON! INTEGER_LITERAL
              | LT! e:shiftExpression GT!  
             )
      { #bitField = #([BIT], #bitField); }       
    ;

modifier
    :	byteOrderModifier
    |	zipModifier
    ;

zipModifier
    :	"zip"! { #zipModifier = #([ZIP, "ZIP"], #zipModifier); }
    ;

byteOrderModifier
    :   "big"!
        { #byteOrderModifier = #([BIG, "BIG_ENDIAN"], #byteOrderModifier); }
    |   "little"!
        { #byteOrderModifier = #([LITTLE, "LITTLE_ENDIAN"], #byteOrderModifier); }
    ;

arrayRange
    :   LBRACKET! (r:rangeExpression)? RBRACKET!
    ;

/*********************************************************************/

sqlDatabaseDefinition[AST doc]
    : "sql_database"! ID 
      LCURLY! 
      (sqlPragmaBlock)? 
      (sqlMetadataBlock)? 
      (sqlTableField)+ 
      (sqlConstraint SEMICOLON! )?
      RCURLY!
      { #sqlDatabaseDefinition = #([SQL_DATABASE], sqlDatabaseDefinition, doc); }
    ;
    
sqlPragmaBlock
    : "sql_pragma"! LCURLY! (sqlPragma)+ RCURLY! SEMICOLON!
      { #sqlPragmaBlock = #([SQL_PRAGMA], sqlPragmaBlock); }
    ;
    
sqlPragma
    : (DOC)? sqlPragmaType ID (fieldInitializer)? (fieldCondition)? SEMICOLON!
      { #sqlPragma = #([FIELD], sqlPragma); }
    ;    

sqlPragmaType
    :   integerType
    |   "string"     
    ;

sqlMetadataBlock
    : "sql_metadata"! LCURLY! (sqlMetadataField)+ RCURLY! SEMICOLON!    
      { #sqlMetadataBlock = #([SQL_METADATA], sqlMetadataBlock); }
    ;
    
sqlMetadataField!
    : (d:DOC)?
      t:typeReference
      f:ID
      (i:fieldInitializer)? 
      (c:fieldCondition)?
      SEMICOLON!
      { #sqlMetadataField = #([FIELD], t, f, i, c, d); }
    ;    

sqlTableField
    : (DOC)? sqlTableDefinition
      { #sqlTableField = #([FIELD], #sqlTableField); }
    ;
      
sqlTableDefinition
    : sqlTableDeclaration (ID)? SEMICOLON!
    | sqlTableReference ID SEMICOLON! 
    ;

sqlTableDeclaration
    : "sql_table"! ID LCURLY! 
      (sqlFieldDefinition)+
      (sqlConstraint SEMICOLON!)?
      RCURLY!
      {#sqlTableDeclaration = #([SQL_TABLE], #sqlTableDeclaration);}
    ;
    
sqlTableReference
    : ID
      {#sqlTableReference = #([TYPEREF], #sqlTableReference);}
    ;    
    
sqlFieldDefinition!
    : (d:DOC)? t:definedType i:ID (c:fieldCondition)? (k:"sql_key")? (s:sqlConstraint)? SEMICOLON!
      { #sqlFieldDefinition = #([FIELD], t, i, c, k, s, d); }
    ;
    
sqlConstraint
    : "sql"! STRING_LITERAL (COMMA! STRING_LITERAL)*     
      { #sqlConstraint = #([SQL, "SQL_CONSTRAINT"], #sqlConstraint); }
    ;  
    
sqlIntegerDeclaration
    : "sql_integer"! ID LCURLY! (sqlIntegerFieldDefinition)+ RCURLY! 
      { #sqlIntegerDeclaration = #([SQL_INTEGER], #sqlIntegerDeclaration); }
    ;

sqlIntegerFieldDefinition!
    : (d:DOC)? t:integerType i:ID (c:fieldCondition)? SEMICOLON!
      { #sqlIntegerFieldDefinition = #([FIELD], t, i, c, d); }
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
    :   expression (RANGE!expression)?
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
    |   sumFunction
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

sizeOfOperand!
    :   "sizeof"^ e:unaryExpression
        { #sizeOfOperand = #([SIZEOF, "SIZEOF"], e); }
    ;

lengthOfOperand!
    :   "lengthof"^ e:unaryExpression
        { #lengthOfOperand = #([LENGTHOF, "LENGTHOF"], e); }
    ;

sumFunction
    :   "sum"  LPAREN! a:functionArgument RPAREN!
        { #sumFunction = #([SUM, "SUM"], a); }
    ;

postfixExpression!
    :   e:primaryExpression  { #postfixExpression = #e; }
        (o:postfixOperand 
                          { if (#o != null)
                          	{
                          		AST rhs = #o.getFirstChild(); 
                            	#postfixExpression= #(o, postfixExpression, rhs);
                            }
                          }
        )*
    ;
    
postfixOperand
    :   arrayOperand 
    |   functionArgumentList 
    |   dotOperand 
    |   choiceOperand
    |   indexOperand
    ;

choiceOperand
    :   "is"^ ID
    ;

arrayOperand!
    :   LBRACKET e:expression RBRACKET
        { #arrayOperand = #([ARRAYELEM, "ARRAYELEM"], e); }
    ;

functionArgumentList
    :   LPAREN! (functionArgument (COMMA! functionArgument)* )? RPAREN!
        { #functionArgumentList = #([FUNCTIONCALL, "FUNCTIONCALL"], functionArgumentList); }
    ;

dotOperand
    :   DOT^ ID<AST=datascript.ast.Expression>
    ;

indexOperand!
    :   DOLLAR "index"
        { #indexOperand = #[INDEX]; }
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
    :   ID<AST=datascript.ast.Expression>
    ;

functionArgument
    :   assignmentExpression
    ;

constant
    :   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;
