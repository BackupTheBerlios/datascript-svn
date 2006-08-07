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
    
    public void reportError(RecognitionException ex) {
        System.out.println(ex.toString());
        throw new RuntimeException(ex);
    }
}

translationUnit
    :   { em.beginTranslationUnit(); }
        #(MEMBERS (declaration)*)
        { em.endTranslationUnit(); }
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
    :   #(PARAM (definedType ID)+)
    ;



/******************* begin of enumerator stuff *****************/

enumDeclaration
    : #(e:"enum" 		{ em.beginEnumeration(e); } 
        builtinType (ID)? 
        enumMemberList)
        			{ em.endEnumeration(e); }
    ;

enumMemberList
    : #(MEMBERS (enumItem)+)
    ;

enumItem
    : #(i:ITEM 			{ em.beginEnumItem(i); }
        (DOC)? ID 
        (expression)?)
        			{ em.endEnumItem(i); }
    ;

bitmaskDeclaration
    : #("bitmask"  builtinType (ID)? enumMemberList)
    ;

constDeclaration
    : #("const" typeReference ID expression)
    ;

fieldDefinition
    :   #(f:FIELD typeReference 
    	  (i:ID					{ em.beginField(f); }
    	  )? 
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
    :   #(s:STRUCT 		{ em.beginSequence(s); }
         (ID)? 
         (parameterList)? 
         memberList)
         			{ em.endSequence(s); }
    ;

unionDeclaration
    :   #(u:UNION 		{ em.beginUnion(u); }
          (ID)? 
          (parameterList)? 
          memberList)
          			{ em.endUnion(u); }
    ;

memberList
    :    #(MEMBERS (declaration)*)
    ;

definedType
    :  #(TYPEREF ID (DOT ID)*) 
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
    |   #(DOT expression)
    |   #(ARRAYELEM expression)
    |   #(INST (expression)+)
    |   #(LPAREN expression)
    |   #("is" ID)
    |   ID
    |   INTEGER_LITERAL 
    |   STRING_LITERAL
    ;


