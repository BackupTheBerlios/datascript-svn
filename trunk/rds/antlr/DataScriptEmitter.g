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
import datascript.emit.Emitter;
}

class DataScriptEmitter extends TreeParser;

options
{
    importVocab=DataScriptParser;
}

{
    private Emitter em;
    
    public void setEmitter(Emitter em)
    {
        this.em = em;
    }
    
    public void reportError(RecognitionException ex)
    {
        System.out.println(ex.toString());
        throw new RuntimeException(ex);
    }
}

root : #(r:ROOT (translationUnit[#r])+ );

translationUnit[AST r]
    :   #(u:TRANSLATION_UNIT { em.beginTranslationUnit(r, u); }
	      (packageDeclaration)? 
	      (importDeclaration)* 
	      members
	    )
		{ em.endTranslationUnit(); }
	;

packageDeclaration
    :   #(p:PACKAGE		{ em.beginPackage(p); }
    	  (ID)+
    	)
    	{ em.endPackage(p); }
    ;
    
importDeclaration
    :   #(i:IMPORT 		{ em.beginImport(i); }
    	  (ID)+
    	)
    	{ em.endImport(); }
    ;
        	
members
    :   { em.beginMembers(); }
    	#(MEMBERS (declaration)*)
    	{ em.endMembers(); }
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
    : #(e:"enum" 		{ em.beginEnumeration(e); } 
        builtinType (ID)? 
        enumMemberList
       )
       { em.endEnumeration(e); }
    ;

enumMemberList
    : #(MEMBERS (enumItem)+)
    ;

enumItem
    : #(i:ITEM 			{ em.beginEnumItem(i); }
        (DOC)? ID 
        (expression)?
       )
       { em.endEnumItem(i); }
    ;

bitmaskDeclaration
    : #("bitmask"  builtinType (ID)? enumMemberList)
    ;

constDeclaration
    : #("const" typeReference ID expression)
    ;

fieldDefinition
    :   #(f:FIELD 
           ((zipModifier typeReference ID) => zipModifier)?
    	   typeReference
    	   (i:ID					{ em.beginField(f); })? 
           (fieldInitializer)?
           (fieldOptionalClause)?
           (fieldCondition)? (DOC)? (label)?
         )
         { if (i != null) em.endField(f); }
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
    :   #(s:SEQUENCE 		{ em.beginSequence(s); }
           (ID)? 
           (parameterList)? 
           memberList
         )
         { em.endSequence(s); }
    ;

unionDeclaration
    :   #(u:UNION 		{ em.beginUnion(u); }
           (ID)? 
           (parameterList)? 
           memberList
         )
         { em.endUnion(u); }
    ;

memberList
    :   #(MEMBERS (declaration)*)
    ;

definedType
    :	#(TYPEREF ID (DOT ID)*) 
    |   builtinType
    ;

subtypeDeclaration
    :	#(s:SUBTYPE     { em.beginSubtype(s); } 
           definedType 
           ID 
           (expression)? 
           (DOC)?
         )
         { em.endSubtype(s); }
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    :   integerType
    |   stringType
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

stringType
	:	STRING
	;

bitField
    :   #(BIT expression)
    ;


modifier
	:	byteOrderModifier
	|	zipModifier
	;

zipModifier
	:	#(ZIP "zip")
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
    : #(d:SQL_DATABASE          { em.beginSqlDatabase(d); }
        ID 
        (sqlPragmaBlock)? 
        (sqlMetadataBlock)? 
        (sqlTableField)+ 
        (sqlConstraint)?
        (DOC)? 
       )                        { em.endSqlDatabase(d); }
    ;
    
sqlPragmaBlock			{ em.beginSqlPragma(p); }
    : #(p:SQL_PRAGMA 
        (sqlPragma)+)           { em.endSqlPragma(p); }
    ;
    
sqlPragma
    : #(FIELD (DOC)? sqlPragmaType ID (fieldInitializer)? (fieldCondition)?)
    ;    

sqlPragmaType
    :   integerType
    |   "string"     
    ;

sqlMetadataBlock
    : #(m:SQL_METADATA          { em.beginSqlMetadata(m); }
        (sqlMetadataField)+ )   { em.endSqlMetadata(m); }
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
    : #(t:SQL_TABLE               { em.beginSqlTable(t); }
        ID
        (sqlFieldDefinition)+
        (sqlConstraint)?
      )                           { em.endSqlTable(t); }
    ;
    
sqlFieldDefinition
    : #(FIELD definedType ID (fieldCondition)? 
        (SQL_KEY)? (sqlConstraint)? (DOC)?)
    ;
    
sqlConstraint
    : #(SQL (STRING_LITERAL)+)
    ;  
    
sqlIntegerDeclaration
    : #(i:SQL_INTEGER              { em.beginSqlInteger(i); }
        (DOC)? 
        (sqlIntegerFieldDefinition)+ 
        )		           { em.endSqlInteger(i); }
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
    |   #(DOT expression)
    |   #(ARRAYELEM expression)
    |   #(INST (expression)+)
    |   #(LPAREN expression)
    |   #("is" ID)
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;


