header
{
package datascript.antlr;
import datascript.ast.*;
import datascript.tools.ToolContext;
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
    :   #(PARAM (typeDeclaration ID)*)
    ;



/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #(e:"enum" builtinType 
        (i:ID                
        )? 
        enumMemberList)
    ;

enumMemberList
{ IntegerValue v = new IntegerValue(0); }
    : #(MEMBERS (v=enumItem[v])+)
    ;

enumItem[IntegerValue last]
returns [IntegerValue self]
{ self = last.add(new IntegerValue(1)); }
    : #(f:ITEM (DOC)? i:ID
        (e:expression   		{ self = (IntegerValue)((Expression)e).getValue(); }
         )?
       )
      { ((EnumItem)f).setValue(self); }
    ;

bitmaskDeclaration
    : #("bitmask"  builtinType (ID)? enumMemberList)
    ;

constDeclaration
    : #("const" typeReference ID expression)
    ;

fieldDefinition
    :   #(FIELD
          typeReference 
          (i:ID)? 
          (in:fieldInitializer)?
          (o:fieldOptionalClause)?
          (c:fieldCondition)? 
          (DOC)? 
          (l:label)?
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
      { ((TypeInstantiation)#INST).checkArguments(); }
    ;
    
structDeclaration
    :   #(s:STRUCT              { pushScope(((StructType)s).getScope()); }
          (i:ID)? 
          (parameterList)? 
          memberList)		{ popScope(); }
          
    ;

unionDeclaration
    :   #(u:UNION 		{ pushScope(((UnionType)u).getScope()); }
          (i:ID)? 
          (parameterList)? 
          memberList)		{  popScope(); }
    ;

memberList
    :    #(MEMBERS (declaration)*)
    ;

definedType
    :  #(t:TYPEREF ID (DOT ID)*) 
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
    :   (integerExpression (integerExpression)?)?
    ;

typeValue
    :   expression
    |   #(LCURLY (typeValue)+)
    ;



// ------- expressions ----------------------------------------------------

expression
    : atom
    | opExpression
    ;

opExpression
    : ( #(COMMA expression expression)
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
      )
      { ((Expression)#opExpression).evaluate(); }
    ;	

atom
    :   id:ID 			{ ((Expression)id).evaluate(scope()); }
    |   il:INTEGER_LITERAL 	{ ((IntegerExpression)il).evaluate(); }	
    |   sl:STRING_LITERAL
    ;

integerExpression
    : e:expression		{ ((Expression)e).checkInteger(); }
    ;
