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

class DataScriptWalker extends TreeParser;

options
{
    importVocab=DataScriptParser;
}

{
    private ToolContext context;
    
    public void setContext(ToolContext context)
    {
        this.context = context;
    }
    
    public void reportError(RecognitionException ex) 
    {
        System.out.println(ex.toString());
        throw new RuntimeException(ex);
    }
}

translationUnit
    :   #(ROOT (packageDeclaration)? (importDeclaration)* members)
    ;    

packageDeclaration
    :   #(PACKAGE (ID)+)
    ;
    
importDeclaration
    :   #(IMPORT (ID)+)
    ;
        	
members
    :   #(MEMBERS (declaration)*)
    ;
    
declaration
    :   fieldDefinition 
    //|   conditionDefinition
    |   constDeclaration 
    |   subtypeDeclaration
    |   sqlDatabaseDefinition
    |   sqlTableDeclaration
    |   sqlIntegerDeclaration
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
    :   #(PARAMLIST (parameterDefinition)+)
    ;

parameterDefinition
    :   #(PARAM definedType ID)
    ;

/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #("enum" builtinType (ID)? enumMemberList)
    ;

enumMemberList
    : #(MEMBERS (enumItem)+)
    ;

enumItem
    : #(ITEM (DOC)? ID (expression)?)
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
    :   sequenceDeclaration
    |   unionDeclaration
    |   enumDeclaration
    |   bitmaskDeclaration
    |   arrayType
    ;

typeReference
    :   sequenceDeclaration
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
    
sequenceDeclaration
    :   #(SEQUENCE (ID)? (parameterList)? memberList)
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

subtypeDeclaration
    : #(SUBTYPE definedType ID (expression)?)
    ;


builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

stringType
	:	STRING
	;

builtinTypeDefaultOrder
    :   integerType
    |   stringType		/*"string"*/
    |   bitField
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


bitField
    :   #(BIT expression)
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

/*********************************************************************/

sqlDatabaseDefinition
    : #(SQL_DATABASE ID 
        (sqlPragmaBlock)? 
        (sqlMetadataBlock)? 
        (sqlTableField)+ 
        (sqlConstraint)?
       )
    ;
    
sqlPragmaBlock
    : #(SQL_PRAGMA (sqlPragma)+)
    ;
    
sqlPragma
    : #(FIELD (DOC)? sqlPragmaType ID (fieldInitializer)? (fieldCondition)?)
    ;    

sqlPragmaType
    :   integerType
    |   "string"     
    ;

sqlMetadataBlock
    : #(SQL_METADATA (sqlMetadataField)+ )
    ;
    
sqlMetadataField
    : #(FIELD typeReference
        ID
        (fieldInitializer)? 
        (fieldCondition)?
        (DOC)?
      )
    ;    

sqlTableField
    : #(FIELD (DOC)? sqlTableDefinition)
    ;
      
sqlTableDefinition
    : sqlTableDeclaration (ID)?  
    | #(TYPEREF ID ID )
    ;

sqlTableDeclaration
    : #(SQL_TABLE ID
        (sqlFieldDefinition)+
        (sqlConstraint)? 
      )
    ;
    
sqlFieldDefinition
    : #(FIELD definedType ID (fieldCondition)? 
        (SQL_KEY)? (sqlConstraint)? (DOC)?)
    ;
    
sqlConstraint
    : #(SQL (STRING_LITERAL)+)
    ;  
    
sqlIntegerDeclaration
    : #(SQL_INTEGER (DOC)? (sqlIntegerFieldDefinition)+ )
    ;
    
sqlIntegerFieldDefinition
    : #(FIELD integerType ID (fieldCondition)? (DOC)? )
    
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
    |   #(SIZEOF expression)
    |   #(LENGTHOF expression)
    |   #(DOT expression expression)
    |   #(ARRAYELEM expression expression)
    |   #(INST (expression)+)
    |   #(LPAREN expression)
    |   #("is" ID)
    |   #(INDEX ID)
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;


