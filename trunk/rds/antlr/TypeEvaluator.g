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
    import datascript.ast.Package;  // explicit to override java.lang.Package
    import datascript.antlr.util.*;
    import java.util.Stack;
}

class TypeEvaluator extends TreeParser;

options
{
    importVocab=DataScriptParser;
}

{
    private Stack<Scope> scopeStack = new Stack<Scope>();
    private ToolContext context;
    private Package pkg = null;


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

root
    : #(ROOT (translationUnit)+ )
    ;

translationUnit
    : #(TRANSLATION_UNIT (packageDeclaration)? (importDeclaration)* members)
    ;    

packageDeclaration
    : #(p:PACKAGE (ID)+)           { pkg = new Package(p); pushScope(pkg); }
    ;

importDeclaration
    : #(i:IMPORT (ID)+)            { pkg.addPackageImport(i); }
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
    : #(p:PARAM definedType i:ID)
    ;



/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #(e:"enum" builtinType 
        (i:ID                           { scope().setTypeSymbol(i, e); }
        )?                              { pushScope(); ((EnumType)e).setScope(scope(), pkg); }
        enumMemberList[e]
      )                                 { popScope(); }
    ;

enumMemberList[AST e]
    : #(MEMBERS (enumItem[e])+)
    ;

enumItem[AST e]
    : #(f:ITEM i:ID (expression)?)
      { scope().setSymbol(i, f); 
        ((EnumItem)f).setEnumType((EnumType)e);
      }
    ;

bitmaskDeclaration
    : #(b:"bitmask"  builtinType (ID)? enumMemberList[b])
    ;

constDeclaration
    : #(c:"const" t:builtinType i:ID expression )
      { scope().setTypeSymbol(i, c); 
        ((ConstType)c).setPackage(pkg);
      }
    ;

fieldDefinition
    : #(FIELD                      { Field f = (Field)#FIELD; 
                                     CompoundType ct = (CompoundType)scope().getOwner();
                                   }
        typeReference
        (i:ID                      { scope().setSymbol(i, f);
                                     f.setName(i);
                                     ct.addField(f);
                                   }
        )? 
        (in:fieldInitializer       { f.setInitializer(in); }
        )?
        (o:fieldOptionalClause     { f.setOptionalClause(o); }
        )?
        (c:fieldCondition          { f.setCondition(c); }
        )? 
        (l:label                   { f.setLabel(l); }
        )?
      ) 
    ;

typeArgumentList
    : (expression)+
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

/*
typeDeclaration
    : sequenceDeclaration
    | unionDeclaration
    | enumDeclaration
    | bitmaskDeclaration
    | arrayType
    ;
*/

typeReference
    : sequenceDeclaration
    | unionDeclaration
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
    : #(INST definedType typeArgumentList)
    ;

sequenceDeclaration
    : #(s:SEQUENCE 
        (i:ID                      { scope().setTypeSymbol(i, s); }
        )?                         { pushScope(); 
                                     ((SequenceType)s).setScope(scope(), pkg);
                                   }
        (parameterList)?  
        memberList
        (functionList)?
      )                            { popScope();
                                     ((SequenceType)s).storeParameters();
                                   }
    ;

unionDeclaration
    : #(u:UNION 
        (i:ID                      { scope().setTypeSymbol(i, u); }
        )?                         { pushScope(); ((UnionType)u).setScope(scope(), pkg); }
        (parameterList)?  
        memberList
        (functionList)?
      )                            { popScope(); ((UnionType)u).storeParameters(); }
    ;

memberList
    : #(MEMBERS (declaration)*)
    ;

functionList
    : #(FUNCTIONS (function)+)
    ;

function
    : #(f:FUNCTION i:ID integerType functionBody) 
      { CompoundType ct = (CompoundType) scope().getOwner();
        ct.addFunction(f); 
        ((FunctionType)f).setOwner(ct);
        scope().setSymbol(i, f);
      }
    ;

functionBody
    : #(RETURN expression)
    ;        

definedType
    : #(t:TYPEREF ID (DOT ID)*) { scope().postLinkAction((TypeReference)t); }
    | builtinType
    ;

subtypeDeclaration
    : #(s:SUBTYPE definedType i:ID (expression)?)
      { scope().setTypeSymbol(i, s); 
        ((Subtype)s).setPackage(pkg);
      }
    ;

builtinType
    : (byteOrderModifier)? builtinTypeDefaultOrder
    ;

builtinTypeDefaultOrder
    : integerType
    | stringType
    | bitField
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
    : (expression (expression)?)?
    ;

typeValue
    : expression
    | #(LCURLY (typeValue)+)
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


/*********************************************************************/

sqlDatabaseDefinition
    : #(s:SQL_DATABASE 
        i:ID                       { scope().setTypeSymbol(i, s); pushScope();
                                     ((CompoundType)s).setScope(scope(), pkg);
                                   }
        (sqlPragmaBlock)? 
        (sqlMetadataBlock)? 
        (sqlTableField)+ 
        (sqlConstraint)? 
       )                           { popScope(); }
    ;

sqlPragmaBlock
    : #(p:SQL_PRAGMA               { pushScope(); 
                                     ((CompoundType)p).setScope(scope(), pkg);
                                   }
        (sqlPragma)+
      )                            { popScope(); }
    ;

sqlPragma
    : #(FIELD                      { Field f = (Field)#FIELD; 
                                     CompoundType ct = (CompoundType) scope().getOwner();
                                   }
        sqlPragmaType 
        n:ID                       { scope().setSymbol(n, f);
                                     f.setName(n);
                                     ct.addField(f);
                                   }
        (i:fieldInitializer        { f.setInitializer(i); }
        )? 
        (c:fieldCondition          { f.setCondition(c); }
        )?
      )
    ;    

sqlPragmaType
    : integerType
    | "string"     
    ;

sqlMetadataBlock
    : #(m:SQL_METADATA             { pushScope(); ((CompoundType)m).setScope(scope(), pkg); }
        (sqlMetadataField)+ 
      )                            { popScope(); }  
    ;

sqlMetadataField
    : #(FIELD 
        typeReference              { Field f = (Field)#FIELD; 
                                     CompoundType ct = (CompoundType) scope().getOwner();
                                   }
        n:ID                       { scope().setSymbol(n, f);
                                     f.setName(n);
                                     ct.addField(f);
                                   }
        (i:fieldInitializer        { f.setInitializer(i); }
        )? 
        (c:fieldCondition          { f.setCondition(c); }
        )?
      )
    ;

sqlTableField
    : #(f:FIELD sqlTableDefinition[f])
    ;

sqlTableDefinition[AST fd]
{ Field f = (Field)#fd;  
  CompoundType ct = (CompoundType) scope().getOwner(); 
}
    : sqlTableDeclaration
      (n:ID                        { scope().setSymbol(n, f);
                                     f.setName(n); 
                                     ct.addField(f);
                                   }
      )?
    | paramTypeInstantiation i:ID  { scope().setSymbol(i, f);
                                     f.setName(i); 
                                     ct.addField(f);
                                   }
    | #(t:TYPEREF ID) 
        m:ID                       { scope().setSymbol(m, f);
                                     f.setName(m); 
                                     ct.addField(f);
                                     scope().postLinkAction((TypeReference)t);          				  
                                   }
    ;

sqlTableDeclaration
    : #(s:SQL_TABLE 
        i:ID                       { scope().setTypeSymbol(i, s); 
    	                             pushScope();
                                     ((CompoundType)s).setScope(scope(), pkg);
                                   }
        (parameterList)? 
        (sqlFieldDefinition)+
        (c:sqlConstraint           { ((SqlTableType)s).setSqlConstraint(c); } 
        )?
      )                            { popScope();
                                     ((CompoundType)s).storeParameters(); 
                                   }
    ;

sqlTableReference
    : paramTypeInstantiation
    | #(TYPEREF sqlTableReference)
    ;

sqlFieldDefinition
    : #(FIELD                      { Field f = (Field)#FIELD; }
        typeReference
        n:ID                       { scope().setSymbol(n, f);
                                     f.setName(n);
                                     CompoundType ct = (CompoundType) scope().getOwner();
                                     ct.addField(f);
                                   }
        (c:fieldCondition)?        { f.setCondition(c); }
        (SQL_KEY)? 
        (sqlConstraint)?
      )
    ;

sqlConstraint
    : #(SQL (STRING_LITERAL)+)
    ;  

sqlIntegerDeclaration
    : #(s:SQL_INTEGER 
        i:ID                       { scope().setTypeSymbol(i, s);
    	                             pushScope(); 
    	                             ((CompoundType)s).setScope(scope(), pkg);
    	                           }
        (sqlIntegerFieldDefinition)+
       )                           { popScope(); }
    ;
    
sqlIntegerFieldDefinition
    : #(FIELD                      { Field f = (Field)#FIELD; 
                                     CompoundType ct = (CompoundType) scope().getOwner();
                                   }
        t:integerType 
        n:ID                       { scope().setSymbol(n, f);
                                     f.setName(n);
                                     ct.addField(f);
                                   }
        (c:fieldCondition          { f.setCondition(c); }
        )? 
      )
    ;


// ------- expressions ----------------------------------------------------

expression
    : COMMA 
    | ASSIGN 
    | MULTASSIGN 
    | DIVASSIGN 
    | MODASSIGN 
    | PLUSASSIGN 
    | MINUSASSIGN 
    | LSHIFTASSIGN 
    | RSHIFTASSIGN 
    | ANDASSIGN 
    | XORASSIGN 
    | ORASSIGN 
    | "forall"
    | QUESTIONMARK
    | LOGICALOR 
    | LOGICALAND 
    | OR 
    | XOR 
    | AND 
    | EQ 
    | NE 
    | LT 
    | GT 
    | LE 
    | GE 
    | LSHIFT 
    | RSHIFT 
    | PLUS 
    | MINUS 
    | MULTIPLY 
    | DIVIDE 
    | MODULO 
    | CAST 
    | UPLUS
    | UMINUS
    | TILDE
    | BANG
    | SIZEOF
    | LENGTHOF
    | DOT
    | ARRAYELEM
    | INST
    | LPAREN
    | FUNCTIONCALL
    | "is"
    | INDEX
    | ID
    | INTEGER_LITERAL 
    | STRING_LITERAL
    | SUM
    ;
