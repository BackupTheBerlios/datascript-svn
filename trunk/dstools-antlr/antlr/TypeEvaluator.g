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
    :   #(PARAM 
          (e:definedType i:ID  		{ scope().setSymbol(i, e); }
          )+
        )
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
          (DOC)? 
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
    :   #(s:STRUCT 
          (i:ID      		{ scope().setSymbol(i, s); }
          )?			{ pushScope(); ((StructType)s).setScope(scope()); }
          (parameterList)? 	
          memberList)		{ popScope(); 
                                  ((StructType)s).storeParameters(); 
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
    |   #("sizeof" expression)
    |   #("lengthof" expression)
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
    |   "sizeof"
    |   "lengthof"
    |   DOT
    |   ARRAYELEM
    |   INST
    |   LPAREN
    |   "is"
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;
