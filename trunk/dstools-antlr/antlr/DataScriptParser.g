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
import datascript.tools.ToolContext;
}

class DataScriptParser extends Parser;

options
{
    k=2;
    buildAST=true;
}

tokens
{
    FIELD<AST=datascript.ast.Field>;
    SEQUENCE<AST=datascript.ast.SequenceType>;
    UNION<AST=datascript.ast.UnionType>;
    PARAM;
    PARAMLIST;
    INST<AST=datascript.ast.TypeInstantiation>;
    ARRAY<AST=datascript.ast.ArrayType>;
    INDEX<AST=datascript.ast.Expression>;
    LABEL;
    BLOCK;
    CAST;
    ROOT;
    MEMBERS;
    PACKAGE;
    IMPORT;
    TYPEREF<AST=datascript.ast.TypeReference>;
    SUBTYPE<AST=datascript.ast.Subtype>;
    UPLUS<AST=datascript.ast.IntegerExpression>;
    UMINUS<AST=datascript.ast.IntegerExpression>;
    ITEM<AST=datascript.ast.EnumItem>;
    BIT<AST=datascript.ast.BitFieldType>;
    UINT8<AST=datascript.ast.StdIntegerType>;
    UINT16<AST=datascript.ast.StdIntegerType>;
    UINT32<AST=datascript.ast.StdIntegerType>;
    UINT64<AST=datascript.ast.StdIntegerType>;
    INT8<AST=datascript.ast.StdIntegerType>;
    INT16<AST=datascript.ast.StdIntegerType>;
    INT32<AST=datascript.ast.StdIntegerType>;
    INT64<AST=datascript.ast.StdIntegerType>;
    ARRAYELEM<AST=datascript.ast.Expression>;
    INTEGER_LITERAL<AST=datascript.ast.IntegerExpression>;
    PLUS<AST=datascript.ast.IntegerExpression>;
    MINUS<AST=datascript.ast.IntegerExpression>;
    MULTIPLY<AST=datascript.ast.IntegerExpression>;
    DIVIDE<AST=datascript.ast.IntegerExpression>;
    MODULO<AST=datascript.ast.IntegerExpression>;
    LSHIFT<AST=datascript.ast.IntegerExpression>;
    RSHIFT<AST=datascript.ast.IntegerExpression>;
    OR<AST=datascript.ast.IntegerExpression>;
    XOR<AST=datascript.ast.IntegerExpression>;
    AND<AST=datascript.ast.IntegerExpression>;
    TILDE<AST=datascript.ast.IntegerExpression>;
    LENGTHOF<AST=datascript.ast.IntegerExpression>;
    SIZEOF<AST=datascript.ast.IntegerExpression>;
    EQ<AST=datascript.ast.BooleanExpression>;
    NE<AST=datascript.ast.BooleanExpression>;
    LT<AST=datascript.ast.BooleanExpression>;
    LE<AST=datascript.ast.BooleanExpression>;
    GE<AST=datascript.ast.BooleanExpression>;
    GT<AST=datascript.ast.BooleanExpression>;
    LOGICALOR<AST=datascript.ast.BooleanExpression>;
    LOGICALAND<AST=datascript.ast.BooleanExpression>;
    BANG<AST=datascript.ast.BooleanExpression>;
    QUESTIONMARK<AST=datascript.ast.Expression>;
    DOT<AST=datascript.ast.Expression>;
    LPAREN<AST=datascript.ast.Expression>;
    
    SQL;
    SQL_DATABASE<AST=datascript.ast.SqlDatabaseType>;
    SQL_PRAGMA<AST=datascript.ast.SqlPragmaType>;
    SQL_METADATA<AST=datascript.ast.SqlMetadataType>;
    SQL_TABLE<AST=datascript.ast.SqlTableType>;
    SQL_INTEGER<AST=datascript.ast.SqlIntegerType>;
}

{
    private ToolContext context;
    
    public void setContext(ToolContext context)
    {
        this.context = context;
    }
}


translationUnit
    : (packageDeclaration)? (importDeclaration)*
      declarationList EOF!
      { #translationUnit = #([ROOT, "ROOT"], translationUnit); }
    ;    

packageDeclaration
    : "package"! ID (DOT! ID)* SEMICOLON!
      { #packageDeclaration = #([PACKAGE, "PACKAGE"], packageDeclaration); }
    ;
    
importDeclaration
    : "import"! ID (DOT! ID)* DOT! MULTIPLY! SEMICOLON!
      { #importDeclaration = #([IMPORT, "IMPORT"], importDeclaration); }
    ;   

declarationList
    :   (declaration)*
        { #declarationList = #([MEMBERS], declarationList); }
    ;


declaration
    :   fieldDefinition SEMICOLON!
    |   conditionDefinition
    |   constDeclaration  SEMICOLON!
    |   subtypeDeclaration SEMICOLON!
    |   sqlDatabaseDefinition  SEMICOLON!
    |   sqlTableDeclaration  SEMICOLON!
//    |   sqlIntegerDeclaration  SEMICOLON!
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
    : "enum"^<AST=datascript.ast.EnumType> 
      builtinType 
      (ID)? 
      enumMemberList
    ;

enumMemberList
    : LCURLY! enumItem (COMMA! enumItem)* RCURLY!
      { #enumMemberList = #([MEMBERS], enumMemberList); }
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
        sequenceDeclaration
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    |   sqlIntegerDeclaration
    |   sqlTableDeclaration
    ;

typeReference
    :   ( (byteOrderModifier)? ("union")? (ID)? (LPAREN parameterDefinition | LCURLY)) =>
        sequenceDeclaration
    |   (ID LPAREN) => paramTypeInstantiation
    |   definedType
    |   enumDeclaration
    |   bitmaskDeclaration
    ;

paramTypeInstantiation
    :   definedType typeArgumentList
        { #paramTypeInstantiation = #([INST, "INST"], paramTypeInstantiation); }
    ;
sequenceDeclaration!
    :   (byteOrderModifier)? 
        (u:"union")? 
        (n:ID)?
        (p:parameterList)? m:memberList
        { if (u == null)
          {
              #sequenceDeclaration = #([SEQUENCE, "sequence"], n, p, m); 
          }
          else
          {
              #sequenceDeclaration = #([UNION, "union"], n, p, m); 
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

// TODO: definedType includes typeSymbols with dotOperands
// Is this really what we want?

subtypeDeclaration
    :   "subtype"! definedType ID (COLON! expression)?
        { #subtypeDeclaration = #([SUBTYPE], #subtypeDeclaration); }
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

integerType!
    :   "uint8"      {#integerType = #[UINT8];}
    |   "uint16"     {#integerType = #[UINT16];}
    |   "uint32"     {#integerType = #[UINT32];}
    |   "uint64"     {#integerType = #[UINT64];}
    |   "int8"       {#integerType = #[INT8];}
    |   "int16"      {#integerType = #[INT16];}
    |   "int32"      {#integerType = #[INT32];}
    |   "int64"      {#integerType = #[INT64];}
    ;

builtinTypeDefaultOrder
    :   integerType
    |   "string"     
    |   bitField
    ;

bitField
    :   "bit"! (  COLON! INTEGER_LITERAL
               | LT! e:shiftExpression GT!  
               )
        { #bitField = #([BIT], #bitField); }       
    ;


byteOrderModifier
    :   "big"
    |   "little"
    ;

arrayRange
    :   LBRACKET! (r:rangeExpression)? RBRACKET!
    ;

/*********************************************************************/

sqlDatabaseDefinition
    : "sql_database"! ID LCURLY! 
      (sqlPragmaBlock)? 
      (sqlMetadataBlock)? 
      (sqlTableField)+ 
      (sqlConstraint SEMICOLON! )?
      RCURLY!
      { #sqlDatabaseDefinition = #([SQL_DATABASE], sqlDatabaseDefinition); }
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
      { #sqlMetadataField = #([FIELD, "field"], t, f, i, c, d); }
    ;    

sqlTableField
    : (DOC)? sqlTableDefinition
      { #sqlTableField = #([FIELD, "FIELD"], #sqlTableField); }
    ;
      
sqlTableDefinition
    : sqlTableDeclaration (ID)? SEMICOLON!
    | sqlTableReference ID SEMICOLON! 
      {#sqlTableDefinition = #([TYPEREF], #sqlTableDefinition);}
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
    ;    
    
sqlFieldDefinition!
    : (d:DOC)? t:definedType i:ID (c:fieldCondition)? (k:"sql_key")? (s:sqlConstraint)? SEMICOLON!
      { #sqlFieldDefinition = #([FIELD, "field"], t, i, c, k, s, d); }
    ;
    
sqlConstraint
    : "sql"! STRING_LITERAL (COMMA! STRING_LITERAL)*     
      { #sqlConstraint = #([SQL], #sqlConstraint); }
    ;  
    
sqlIntegerDeclaration!
    : (d:DOC)? "sql_integer"! LCURLY! s:sqlIntegerFields RCURLY! SEMICOLON!
      { #sqlIntegerDeclaration = #([SQL_INTEGER, "SQL_INTEGER"], d, s); }
    ;
    
sqlIntegerFields
    : (sqlIntegerFieldDefinition)+    
    ;
    
sqlIntegerFieldDefinition!
    : (d:DOC)? t:integerType i:ID (c:fieldCondition)? SEMICOLON!
      { #sqlIntegerFieldDefinition = #([FIELD, "FIELD"], t, i, c, d); }
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

postfixExpression!
    :   e:primaryExpression  { #postfixExpression = #e; }
        (o:postfixOperand 
                          { AST rhs = #o.getFirstChild(); 
                            #postfixExpression= #(o, postfixExpression, rhs); }
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
        { #functionArgumentList = #([INST, "INST"], functionArgumentList); }
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
