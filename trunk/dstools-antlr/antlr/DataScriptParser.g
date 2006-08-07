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
    STRUCT<AST=datascript.ast.StructType>;
    UNION<AST=datascript.ast.UnionType>;
    PARAM;
    INST<AST=datascript.ast.TypeInstantiation>;
    ARRAY<AST=datascript.ast.ArrayType>;
    LABEL;
    BLOCK;
    CAST;
    MEMBERS;
    TYPEREF<AST=datascript.ast.TypeReference>;
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
}

{
    private ToolContext context;
    
    public void setContext(ToolContext context)
    {
        this.context = context;
    }
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
    : definedType ID
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
        { #arrayOperand = #([ARRAYELEM, "ARRAYELEM"], e); }
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
    :   ID<AST=datascript.ast.Expression>
    ;

functionArgument
    :   assignmentExpression
    ;

constant
    :   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;
