//
// Generated by JTB 1.2.2
//

package datascript.visitor;
import datascript.syntaxtree.*;
import java.util.*;

/**
 * All Object visitors must implement this interface.
 */
public interface ObjectVisitor {
   //
   // Object Auto class visitors
   //
   public Object visit(NodeList n, Object argu);
   public Object visit(NodeListOptional n, Object argu);
   public Object visit(NodeOptional n, Object argu);
   public Object visit(NodeSequence n, Object argu);
   public Object visit(NodeToken n, Object argu);

   //
   // User-generated visitor methods below
   //

   /**
    * <PRE>
    * declarationList -> DeclarationList()
    * </PRE>
    */
   public Object visit(TranslationUnit n, Object argu);

   /**
    * <PRE>
    * nodeListOptional -> ( Declaration() )*
    * </PRE>
    */
   public Object visit(DeclarationList n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> FieldDefinition() ";"
    *       | ConditionDefinition()
    *       | ConstDeclaration() ";"
    *       | SqlFieldDefinition() ";"
    * </PRE>
    */
   public Object visit(Declaration n, Object argu);

   /**
    * <PRE>
    * nodeOptional -> [ GlobalLabel() ]
    * expression -> Expression()
    * nodeToken -> ":"
    * </PRE>
    */
   public Object visit(Label n, Object argu);

   /**
    * <PRE>
    * expression -> Expression()
    * nodeToken -> "::"
    * </PRE>
    */
   public Object visit(GlobalLabel n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;CONDITION&gt;
    * nodeToken1 -> &lt;IDENTIFIER&gt;
    * nodeToken2 -> "("
    * nodeOptional -> [ ParameterDefinition() ( "," ParameterDefinition() )* ]
    * nodeToken3 -> ")"
    * nodeToken4 -> "{"
    * nodeListOptional -> ( ConditionExpression() ";" )*
    * nodeToken5 -> "}"
    * </PRE>
    */
   public Object visit(ConditionDefinition n, Object argu);

   /**
    * <PRE>
    * typeDeclaration -> TypeDeclaration()
    * nodeToken -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public Object visit(ParameterDefinition n, Object argu);

   /**
    * <PRE>
    * expression -> Expression()
    * </PRE>
    */
   public Object visit(ConditionExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;ENUM&gt; | &lt;BITMASK&gt; )
    * builtinType -> BuiltinType()
    * nodeOptional -> [ &lt;IDENTIFIER&gt; ]
    * nodeToken -> "{"
    * enumItem -> EnumItem()
    * nodeListOptional -> ( "," EnumItem() )*
    * nodeToken1 -> "}"
    * </PRE>
    */
   public Object visit(EnumDeclaration n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IDENTIFIER&gt;
    * nodeOptional -> [ "=" ConstantExpression() ]
    * </PRE>
    */
   public Object visit(EnumItem n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;CONST&gt;
    * typeDeclaration -> TypeDeclaration()
    * nodeToken1 -> &lt;IDENTIFIER&gt;
    * nodeToken2 -> "="
    * typeValue -> TypeValue()
    * </PRE>
    */
   public Object visit(ConstDeclaration n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ConstantExpression()
    *       | "{" TypeValueList() "}"
    * </PRE>
    */
   public Object visit(TypeValue n, Object argu);

   /**
    * <PRE>
    * typeValue -> TypeValue()
    * nodeListOptional -> ( "," TypeValue() )*
    * </PRE>
    */
   public Object visit(TypeValueList n, Object argu);

   /**
    * <PRE>
    * sqlType -> SqlType()
    * nodeOptional -> [ &lt;IDENTIFIER&gt; ]
    * nodeSequence -> ( SqlFieldContent() )
    * nodeListOptional -> ( &lt;STRING&gt; )*
    * </PRE>
    */
   public Object visit(SqlFieldDefinition n, Object argu);

   /**
    * <PRE>
    * sqlSyntax -> SqlSyntax()
    * nodeListOptional -> ( "," SqlSyntax() )*
    * </PRE>
    */
   public Object visit(SqlFieldContent n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> &lt;INDEX&gt;
    *       | &lt;CONSTRAINT&gt;
    * </PRE>
    */
   public Object visit(SqlType n, Object argu);

   /**
    * <PRE>
    * nodeOptional -> [ Label() ]
    * typeDeclaration -> TypeDeclaration()
    * nodeOptional1 -> [ TypeArgumentList() ]
    * nodeOptional2 -> [ &lt;IDENTIFIER&gt; ]
    * nodeListOptional -> ( ArrayRange() )*
    * nodeOptional3 -> [ FieldInitializer() ]
    * nodeOptional4 -> [ FieldCondition() ]
    * nodeOptional5 -> [ SqlCondition() ]
    * nodeOptional6 -> [ FieldOptionalClause() ]
    * nodeListOptional1 -> ( &lt;STRING&gt; )*
    * </PRE>
    */
   public Object visit(FieldDefinition n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "("
    * nodeOptional -> [ FunctionArgument() ( "," FunctionArgument() )* ]
    * nodeToken1 -> ")"
    * </PRE>
    */
   public Object visit(TypeArgumentList n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "="
    * typeValue -> TypeValue()
    * </PRE>
    */
   public Object visit(FieldInitializer n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IF&gt;
    * expression -> Expression()
    * </PRE>
    */
   public Object visit(FieldOptionalClause n, Object argu);

   /**
    * <PRE>
    * nodeToken -> ":"
    * expression -> Expression()
    * </PRE>
    */
   public Object visit(FieldCondition n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;SQL&gt;
    * sqlSyntax -> SqlSyntax()
    * nodeListOptional -> ( "," SqlSyntax() )*
    * </PRE>
    */
   public Object visit(SqlCondition n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;STRING_LITERAL&gt;
    * </PRE>
    */
   public Object visit(SqlSyntax n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> StructDeclaration()
    *       | DefinedType()
    *       | EnumDeclaration()
    * </PRE>
    */
   public Object visit(TypeDeclaration n, Object argu);

   /**
    * <PRE>
    * nodeOptional -> [ ByteOrderModifier() ]
    * nodeOptional1 -> [ &lt;UNION&gt; | &lt;DATABASE&gt; | &lt;TABLE&gt; | &lt;SQLINTEGER&gt; | &lt;SQLTEXT&gt; ]
    * nodeOptional2 -> [ &lt;IDENTIFIER&gt; ]
    * nodeOptional3 -> [ "(" ParameterDefinition() ( "," ParameterDefinition() )* ")" ]
    * nodeToken -> "{"
    * declarationList -> DeclarationList()
    * nodeToken1 -> "}"
    * </PRE>
    */
   public Object visit(StructDeclaration n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> TypeSymbol()
    *       | BuiltinType()
    * </PRE>
    */
   public Object visit(DefinedType n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IDENTIFIER&gt;
    * nodeListOptional -> ( DotOperand() )*
    * </PRE>
    */
   public Object visit(TypeSymbol n, Object argu);

   /**
    * <PRE>
    * nodeOptional -> [ ByteOrderModifier() ]
    * nodeChoice -> ( &lt;UINT8&gt; | &lt;UINT16&gt; | &lt;UINT32&gt; | &lt;UINT64&gt; | &lt;INT8&gt; | &lt;INT16&gt; | &lt;INT32&gt; | &lt;INT64&gt; | &lt;STRING&gt; | BitField() )
    * </PRE>
    */
   public Object visit(BuiltinType n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;BIT&gt;
    * nodeChoice -> ( ":" &lt;INTEGER_LITERAL&gt; | &lt;LT&gt; ShiftExpression() &lt;GT&gt; )
    * </PRE>
    */
   public Object visit(BitField n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> &lt;BIG&gt;
    *       | &lt;LITTLE&gt;
    * </PRE>
    */
   public Object visit(ByteOrderModifier n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "["
    * nodeOptional -> [ RangeExpression() ]
    * nodeToken1 -> "]"
    * </PRE>
    */
   public Object visit(ArrayRange n, Object argu);

   /**
    * <PRE>
    * assignmentExpression -> AssignmentExpression()
    * nodeListOptional -> ( CommaOperand() )*
    * </PRE>
    */
   public Object visit(Expression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> ","
    * assignmentExpression -> AssignmentExpression()
    * </PRE>
    */
   public Object visit(CommaOperand n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> UnaryExpression() AssignmentOperator() AssignmentExpression()
    *       | QuantifiedExpression()
    * </PRE>
    */
   public Object visit(AssignmentExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "&lt;&lt;=" | "&gt;&gt;=" | "&=" | "^=" | "|=" )
    * </PRE>
    */
   public Object visit(AssignmentOperator n, Object argu);

   /**
    * <PRE>
    * nodeOptional -> [ Quantifier() ]
    * conditionalExpression -> ConditionalExpression()
    * </PRE>
    */
   public Object visit(QuantifiedExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;FORALL&gt;
    * nodeToken1 -> &lt;IDENTIFIER&gt;
    * nodeToken2 -> &lt;IN&gt;
    * unaryExpression -> UnaryExpression()
    * nodeToken3 -> ":"
    * </PRE>
    */
   public Object visit(Quantifier n, Object argu);

   /**
    * <PRE>
    * logicalOrExpression -> LogicalOrExpression()
    * nodeOptional -> [ ConditionalExpressionOperand() ]
    * </PRE>
    */
   public Object visit(ConditionalExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;QUESTIONMARK&gt;
    * expression -> Expression()
    * nodeToken1 -> ":"
    * conditionalExpression -> ConditionalExpression()
    * </PRE>
    */
   public Object visit(ConditionalExpressionOperand n, Object argu);

   /**
    * <PRE>
    * conditionalExpression -> ConditionalExpression()
    * </PRE>
    */
   public Object visit(ConstantExpression n, Object argu);

   /**
    * <PRE>
    * expression -> Expression()
    * nodeOptional -> [ UpperBoundExpression() ]
    * </PRE>
    */
   public Object visit(RangeExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;RANGE&gt;
    * expression -> Expression()
    * </PRE>
    */
   public Object visit(UpperBoundExpression n, Object argu);

   /**
    * <PRE>
    * logicalAndExpression -> LogicalAndExpression()
    * nodeOptional -> [ LogicalOrOperand() ]
    * </PRE>
    */
   public Object visit(LogicalOrExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;LOGICALOR&gt;
    * logicalOrExpression -> LogicalOrExpression()
    * </PRE>
    */
   public Object visit(LogicalOrOperand n, Object argu);

   /**
    * <PRE>
    * inclusiveOrExpression -> InclusiveOrExpression()
    * nodeOptional -> [ LogicalAndOperand() ]
    * </PRE>
    */
   public Object visit(LogicalAndExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;LOGICALAND&gt;
    * logicalAndExpression -> LogicalAndExpression()
    * </PRE>
    */
   public Object visit(LogicalAndOperand n, Object argu);

   /**
    * <PRE>
    * exclusiveOrExpression -> ExclusiveOrExpression()
    * nodeOptional -> [ InclusiveOrOperand() ]
    * </PRE>
    */
   public Object visit(InclusiveOrExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;OR&gt;
    * inclusiveOrExpression -> InclusiveOrExpression()
    * </PRE>
    */
   public Object visit(InclusiveOrOperand n, Object argu);

   /**
    * <PRE>
    * andExpression -> AndExpression()
    * nodeOptional -> [ ExclusiveOrOperand() ]
    * </PRE>
    */
   public Object visit(ExclusiveOrExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;XOR&gt;
    * exclusiveOrExpression -> ExclusiveOrExpression()
    * </PRE>
    */
   public Object visit(ExclusiveOrOperand n, Object argu);

   /**
    * <PRE>
    * equalityExpression -> EqualityExpression()
    * nodeOptional -> [ AndOperand() ]
    * </PRE>
    */
   public Object visit(AndExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;AND&gt;
    * andExpression -> AndExpression()
    * </PRE>
    */
   public Object visit(AndOperand n, Object argu);

   /**
    * <PRE>
    * relationalExpression -> RelationalExpression()
    * nodeOptional -> [ EqualityOperand() ]
    * </PRE>
    */
   public Object visit(EqualityExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;EQ&gt; | &lt;NE&gt; )
    * equalityExpression -> EqualityExpression()
    * </PRE>
    */
   public Object visit(EqualityOperand n, Object argu);

   /**
    * <PRE>
    * shiftExpression -> ShiftExpression()
    * nodeListOptional -> ( RelationalOperand() )*
    * </PRE>
    */
   public Object visit(RelationalExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;LT&gt; | &lt;LE&gt; | &lt;GT&gt; | &lt;GE&gt; )
    * shiftExpression -> ShiftExpression()
    * </PRE>
    */
   public Object visit(RelationalOperand n, Object argu);

   /**
    * <PRE>
    * additiveExpression -> AdditiveExpression()
    * nodeListOptional -> ( ShiftOperand() )*
    * </PRE>
    */
   public Object visit(ShiftExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;SHIFTLEFT&gt; | &lt;SHIFTRIGHT&gt; )
    * additiveExpression -> AdditiveExpression()
    * </PRE>
    */
   public Object visit(ShiftOperand n, Object argu);

   /**
    * <PRE>
    * multiplicativeExpression -> MultiplicativeExpression()
    * nodeListOptional -> ( Summand() )*
    * </PRE>
    */
   public Object visit(AdditiveExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;PLUS&gt; | &lt;MINUS&gt; )
    * multiplicativeExpression -> MultiplicativeExpression()
    * </PRE>
    */
   public Object visit(Summand n, Object argu);

   /**
    * <PRE>
    * castExpression -> CastExpression()
    * nodeListOptional -> ( Multiplicand() )*
    * </PRE>
    */
   public Object visit(MultiplicativeExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;MULTIPLY&gt; | &lt;DIVIDE&gt; | &lt;MODULO&gt; )
    * castExpression -> CastExpression()
    * </PRE>
    */
   public Object visit(Multiplicand n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> CastOperand()
    *       | UnaryExpression()
    * </PRE>
    */
   public Object visit(CastExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "("
    * definedType -> DefinedType()
    * nodeToken1 -> ")"
    * castExpression -> CastExpression()
    * </PRE>
    */
   public Object visit(CastOperand n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> PostfixExpression()
    *       | UnaryOperand()
    *       | SizeOfOperand()
    * </PRE>
    */
   public Object visit(UnaryExpression n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> ( &lt;PLUS&gt; | &lt;MINUS&gt; | &lt;TILDE&gt; | &lt;BANG&gt; )
    * castExpression -> CastExpression()
    * </PRE>
    */
   public Object visit(UnaryOperand n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;SIZEOF&gt;
    * unaryExpression -> UnaryExpression()
    * </PRE>
    */
   public Object visit(SizeOfOperand n, Object argu);

   /**
    * <PRE>
    * primaryExpression -> PrimaryExpression()
    * nodeListOptional -> ( ArrayOperand() | FunctionArgumentList() | DotOperand() | ChoiceOperand() )*
    * </PRE>
    */
   public Object visit(PostfixExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IS&gt;
    * nodeToken1 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public Object visit(ChoiceOperand n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "["
    * expression -> Expression()
    * nodeToken1 -> "]"
    * </PRE>
    */
   public Object visit(ArrayOperand n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "("
    * nodeOptional -> [ FunctionArgument() ( "," FunctionArgument() )* ]
    * nodeToken1 -> ")"
    * </PRE>
    */
   public Object visit(FunctionArgumentList n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "."
    * nodeToken1 -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public Object visit(DotOperand n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> VariableName()
    *       | Constant()
    *       | ParenthesizedExpression()
    * </PRE>
    */
   public Object visit(PrimaryExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> "("
    * expression -> Expression()
    * nodeToken1 -> ")"
    * </PRE>
    */
   public Object visit(ParenthesizedExpression n, Object argu);

   /**
    * <PRE>
    * nodeToken -> &lt;IDENTIFIER&gt;
    * </PRE>
    */
   public Object visit(VariableName n, Object argu);

   /**
    * <PRE>
    * assignmentExpression -> AssignmentExpression()
    * </PRE>
    */
   public Object visit(FunctionArgument n, Object argu);

   /**
    * <PRE>
    * nodeChoice -> &lt;INTEGER_LITERAL&gt;
    *       | &lt;FLOATING_POINT_LITERAL&gt;
    *       | &lt;CHARACTER_LITERAL&gt;
    *       | &lt;STRING_LITERAL&gt;
    * </PRE>
    */
   public Object visit(Constant n, Object argu);

}