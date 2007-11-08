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
    import java.util.Stack;
}

class ExpressionEvaluator extends TreeParser;

options
{
    importVocab=DataScriptParser;
}

{
    private Stack<Scope> scopeStack = new Stack();
    private ToolContext context;


    public void setContext(ToolContext context)
    {
        this.context = context;
    }


    public void reportError(RecognitionException ex) 
    {
        System.out.println(ex.toString());
        //throw new RuntimeException(ex);
    }


    public void pushScope(Scope s)
    {
        scopeStack.push(s);
    }


    private void popScope()
    {
        scopeStack.pop();
    }


    private Scope scope()
    {
        return scopeStack.peek();
    }
}


root
    : #(ROOT (translationUnit)+ )
    ;

translationUnit
    : #(TRANSLATION_UNIT (packageDeclaration)? (importDeclaration)* members)
    ;    

packageDeclaration
    : #(PACKAGE (ID)+)
    ;

importDeclaration
    : #(IMPORT (ID)+)
    ;

members
    : #(MEMBERS (declaration)*)
    ;

declaration
    : fieldDefinition 
    //| conditionDefinition
    | constDeclaration 
    | subtypeDeclaration
    | sqlDatabaseDefinition
    | sqlTableDeclaration
    | sqlIntegerDeclaration
    ;

label
    : #(LABEL expression (expression)?)
    ;

alignment
    : #(ALIGN INTEGER_LITERAL)
    ;

/*
conditionDefinition
    : "condition"^ ID parameterList conditionBlock
    ;

conditionBlock!
    : LCURLY^ (e:conditionExpression SEMICOLON!)* RCURLY!
      { #conditionBlock = #([BLOCK, "BLOCK"], e); }
    ;

conditionExpression
    : expression
    ;
*/

parameterList 
    : #(PARAMLIST (parameterDefinition)+)
    ;

parameterDefinition
    : #(PARAM definedType ID)
    ;



/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #(e:"enum"                   { pushScope(((EnumType)e).getScope()); }
        builtinType 
        (ID)? 
        enumMemberList
      )                            { popScope(); }
    ;

enumMemberList
{ IntegerValue v = new IntegerValue(0); }
    : #(MEMBERS (v=enumItem[v])+)
    ;

enumItem[IntegerValue last]
returns [IntegerValue self]
{ self = last.add(new IntegerValue(1)); }
    : #(f:ITEM 
        ID
        (e:expression              { self = (IntegerValue)((Expression)e).getValue(); }
        )?
      )                            { ((EnumItem)f).setValue(self); }
    ;

bitmaskDeclaration
    : #("bitmask"  builtinType (ID)? enumMemberList)
    ;

constDeclaration
    : #("const" definedType ID expression)
    ;

fieldDefinition
    : #(f:FIELD                    { scope().setCurrentField((Field)f); }
        typeReference 
        (ID)? 
        (fieldInitializer)?
        (fieldOptionalClause)?
        (fieldCondition)? 
        (label)?
        (alignment)?
      ) 
    ;

typeArgumentList
    :   (expression)+
    ;

fieldInitializer
    : #(ASSIGN typeValue)
    ;

fieldOptionalClause
    : #("if" expression)
    ;

fieldCondition
    : #(COLON expression)
    ;

typeReference
    : sequenceDeclaration
    | unionDeclaration
    | choiceDeclaration
    | definedType
    | enumDeclaration
    | bitmaskDeclaration
    | arrayType
    | paramTypeInstantiation
    ;

arrayType
    : #(ARRAY typeReference arrayRange)
    ;

paramTypeInstantiation
    : #(INST 
        definedType 
        typeArgumentList
       )                           { ((TypeInstantiation)#INST).checkArguments(); }
    ;

sequenceDeclaration
    : #(s:SEQUENCE                 { pushScope(((SequenceType)s).getScope()); }
        (ID)? 
        (parameterList)? 
        memberList
        (functionList)?
      )                            { popScope(); }
          
    ;

unionDeclaration
    : #(u:UNION                    { pushScope(((UnionType)u).getScope()); }
        (ID)? 
        (parameterList)? 
        memberList
        (functionList)?
      )                            { popScope(); }
    ;

choiceDeclaration
    :  #(c:CHOICE ID                 { pushScope(((ChoiceType)c).getScope()); } 
         (parameterList)? 
         e:expression                { TypeInterface exprType = ((Expression)e).getExprType(); 
                                       if (exprType instanceof EnumType)
                                            pushScope(((EnumType)exprType).getScope());
         							 } 
         choiceMemberList 
                                     { if (exprType instanceof EnumType)
                                           popScope();
         							 }
         (functionList)? 
        )                          { popScope(); }
    ;
    
choiceMemberList
    :  #(MEMBERS (choiceMember)+ (defaultChoice)?)
    ;
    
choiceMember
    : choiceCases choiceAlternative
    ;
    
choiceCases
    : #(CASE (expression)+)
    ;
    
choiceAlternative
    : #(FIELD typeReference ID)
    ;
    
defaultChoice
    : #(DEFAULT choiceAlternative)        
    ;   
     
memberList
    : #(MEMBERS (declaration)*)
    ;

functionList
    : #(FUNCTIONS (function)+)
    ;

function
    : #(FUNCTION ID definedType functionBody) 
    ;

functionBody
    : #(RETURN expression)
    ;        

definedType
    : #(TYPEREF ID (DOT ID)*) 
    | builtinType
    ;

subtypeDeclaration
    : #(SUBTYPE definedType ID (expression)?)
    ;

builtinType
    : (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    : integerType
    | stringType
    | bitField
    ;

integerType
    : UINT8
    | UINT16
    | UINT32
    | UINT64
    | INT8
    | INT16
    | INT32
    | INT64
    ;

stringType
    : STRING
    ;

bitField
    : #(BIT expression)
    ;


byteOrderModifier
    : "big"
    | "little"
    ;

arrayRange
    : (integerExpression (integerExpression)?)?
    ;

typeValue
    : expression
    | #(LCURLY (typeValue)+)
    ;



/*********************************************************************/

sqlDatabaseDefinition
    : #(d:SQL_DATABASE             { pushScope(((CompoundType)d).getScope()); }
        ID 
        (sqlPragmaBlock)? 
        (sqlMetadataBlock)?   
        (sqlTableField)+ 
        (sqlConstraint)?
       )                           { popScope(); }   
    ;

sqlPragmaBlock
    : #(p:SQL_PRAGMA               { pushScope(((CompoundType)p).getScope()); }
        (sqlPragma)+
      )                            { popScope(); }   
    ;

sqlPragma
    : #(f:FIELD                    { scope().setCurrentField((Field)f); }
        sqlPragmaType 
        ID 
        (fieldInitializer)? 
        (fieldCondition)?
      )
    ;    

sqlPragmaType
    : integerType
    | "string"     
    ;

sqlMetadataBlock
    : #(m:SQL_METADATA             { pushScope(((CompoundType)m).getScope()); }
        (sqlMetadataField)+
      )                            { popScope(); }   
    ;

sqlMetadataField
    : #(f:FIELD                    { scope().setCurrentField((Field)f); }
        typeReference
        ID
        (fieldInitializer)? 
        (fieldCondition)?
      )
    ;    

sqlTableField
    : #(FIELD sqlTableDefinition)
    ;

sqlTableDefinition
    : sqlTableDeclaration (ID)? 
    | paramTypeInstantiation ID 
    | #(TYPEREF ID) ID 
    ;

sqlTableDeclaration
    : #(t:SQL_TABLE                { pushScope(((CompoundType)t).getScope()); }
        ID
        (parameterList)? 
        (sqlFieldDefinition)+
        (sqlConstraint)? 
      )                            { popScope(); }   
    ;

sqlFieldDefinition
    : #(f:FIELD                    { scope().setCurrentField((Field)f); }
        typeReference
        ID 
        (fieldCondition)? 
        (SQL_KEY)? 
        (sqlConstraint)?
      )
    ;

sqlConstraint
    : #(SQL (STRING_LITERAL)+)
    ;  

sqlIntegerDeclaration
    : #(s:SQL_INTEGER              { pushScope(((CompoundType)s).getScope()); }
        ID
        (sqlIntegerFieldDefinition)+
      )                            { popScope(); }
    ;

sqlIntegerFieldDefinition
    : #(f:FIELD                    { scope().setCurrentField((Field)f); }
        integerType 
        ID 
        (fieldCondition)?
       )
    ;    


// ------- expressions ----------------------------------------------------

expression
    : atom
    | opExpression
    | sumFunction
    ;

sumFunction
    : ( #(SUM expression))
      { ((Expression)#sumFunction).evaluate(scope()); }
    ;

opExpression
    : ( #(COMMA expression expression)
      | #(ASSIGN expression expression)
      | #(MULTASSIGN expression expression)
      | #(DIVASSIGN expression expression)
      | #(MODASSIGN expression expression)
      | #(PLUSASSIGN expression expression)
      | #(MINUSASSIGN expression expression)
      | #(LSHIFTASSIGN expression expression)
      | #(RSHIFTASSIGN expression expression)
      | #(ANDASSIGN expression expression)
      | #(XORASSIGN expression expression)
      | #(ORASSIGN expression expression)
      | #("forall" expression expression expression)
      | #(QUESTIONMARK expression expression expression)
      | #(LOGICALOR expression expression)
      | #(LOGICALAND expression expression)
      | #(OR expression expression)
      | #(XOR expression expression)
      | #(AND expression expression)
      | #(EQ expression expression)
      | #(NE expression expression)
      | #(LT expression expression)
      | #(GT expression expression)
      | #(LE expression expression)
      | #(GE expression expression)
      | #(LSHIFT expression expression)
      | #(RSHIFT expression expression)
      | #(PLUS expression expression)
      | #(MINUS expression expression)
      | #(MULTIPLY expression expression)
      | #(DIVIDE expression expression)
      | #(MODULO expression expression)
      | #(CAST definedType expression)
      | #(UPLUS expression)
      | #(UMINUS expression)
      | #(TILDE expression)
      | #(BANG expression)
      | #(SIZEOF expression)
      | #(BITSIZEOF expression)
      | #(LENGTHOF expression)
      | #(DOT expression ID)    
      | #(ARRAYELEM expression expression)
      | #(INST (expression)+)
      | #(LPAREN expression)
      | #(FUNCTIONCALL expression)
      | #("is" identifier)
      | #(INDEX identifier)
      | #(EXPLICIT ID)
      )
      { ((Expression)#opExpression).evaluate(scope()); }
    ;   

atom
    : id:ID                        { ((Expression)id).evaluate(scope()); }
    | il:INTEGER_LITERAL           { ((IntegerExpression)il).evaluate(scope()); }  
    | sl:STRING_LITERAL
    ;

identifier
    : id:ID                        { ((Expression)id).evaluate(scope()); }
    ;

integerExpression
    : e:expression                 { ((Expression)e).checkInteger(); }
    ;
