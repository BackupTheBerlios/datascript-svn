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
import java.util.Stack;
}

class TypeEvaluator extends TreeParser;

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
    
    private void pushScope()
    {
    	Scope inner = new Scope(scope());
    	scopeStack.push(inner);
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
    :   #(p:PARAM definedType i:ID)
    ;


/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #(e:"enum" builtinType 
        (i:ID                		{scope().setSymbol(i, e); }
        )? 
        enumMemberList[e])
    ;

enumMemberList[AST e]
    : #(MEMBERS (enumItem[e])+)
    ;

enumItem[AST e]
    : #(f:ITEM (DOC)? i:ID (expression)?)
      { scope().setSymbol(i, f); 
        ((EnumItem)f).setEnumType((EnumType)e); }
    ;

bitmaskDeclaration
    : #(b:"bitmask"  builtinType (ID)? enumMemberList[b])
    ;

constDeclaration
    : #("const" typeReference ID expression)
    ;

fieldDefinition
    :   #(FIELD                         { Field f = (Field)#FIELD; 
    					  CompoundType ct = scope().getOwner(); }
          typeReference 
          (i:ID 			{ scope().setSymbol(i, f);
          				  f.setName(i);
          				  ct.addField(f);
          				}
          )? 
          (in:fieldInitializer		{f.setInitializer(in); }
          )?
          (o:fieldOptionalClause        {f.setOptionalClause(o); }
          )?
          (c:fieldCondition		{ f.setCondition(c); }
          )? 
          (d:DOC			{ f.setDocumentation(d); }
          )? 
          (l:label			{ f.setLabel(l); }
          )?
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
    :   #(s:SEQUENCE 
          (i:ID      		{ scope().setSymbol(i, s); }
          )?			{ pushScope(); ((SequenceType)s).setScope(scope()); }
          (parameterList)? 	
          memberList)		{ popScope(); 
                                  ((SequenceType)s).storeParameters(); 
                                  }
          
    ;

unionDeclaration
    :   #(u:UNION 
    
          (i:ID      		{ scope().setSymbol(i, u); }
          )?			{ pushScope(); ((UnionType)u).setScope(scope()); }
          (parameterList)? 	
          memberList)		{ popScope(); 
                                  ((UnionType)u).storeParameters();
                                }
    ;

memberList
    :    #(MEMBERS (declaration)*)
    ;

definedType
    :  #(t:TYPEREF ID (DOT ID)*) 	{ ((TypeReference)t).resolve(scope()); }
    |   builtinType
    ;

builtinType
    :   (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    :   UINT8
    |   UINT16
    |   UINT32
    |   UINT64
    |   INT8
    |   INT16
    |   INT32
    |   INT64
    |   "string"
    |   bitField
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



// ------- expressions ----------------------------------------------------

/*
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
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;
*/

expression
    :   COMMA 
    |   ASSIGN 
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
    |   "forall"
    |   QUESTIONMARK
    |   LOGICALOR 
    |   LOGICALAND 
    |   OR 
    |   XOR 
    |   AND 
    |   EQ 
    |   NE 
    |   LT 
    |   GT 
    |   LE 
    |   GE 
    |   LSHIFT 
    |   RSHIFT 
    |   PLUS 
    |   MINUS 
    |   MULTIPLY 
    |   DIVIDE 
    |   MODULO 
    |   CAST 
    |   UPLUS
    |   UMINUS
    |   TILDE
    |   BANG
    |   SIZEOF
    |   LENGTHOF
    |   DOT
    |   ARRAYELEM
    |   INST
    |   LPAREN
    |   "is"
    |   INDEX
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;
