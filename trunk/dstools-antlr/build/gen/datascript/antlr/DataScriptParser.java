// $ANTLR 2.7.6 (2005-12-22): "DataScriptParser.g" -> "DataScriptParser.java"$

package datascript.antlr;

import datascript.ast.*;
import datascript.tools.ToolContext;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

public class DataScriptParser extends antlr.LLkParser       implements DataScriptParserTokenTypes
 {

    private ToolContext context;
    
    public void setContext(ToolContext context)
    {
        this.context = context;
    }

protected DataScriptParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DataScriptParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected DataScriptParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DataScriptParser(TokenStream lexer) {
  this(lexer,2);
}

public DataScriptParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void translationUnit() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST translationUnit_AST = null;
		
		try {      // for error handling
			declarationList();
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			translationUnit_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = translationUnit_AST;
	}
	
	public final void declarationList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declarationList_AST = null;
		
		try {      // for error handling
			{
			_loop4:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					declaration();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				declarationList_AST = (AST)currentAST.root;
				declarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MEMBERS)).add(declarationList_AST));
				currentAST.root = declarationList_AST;
				currentAST.child = declarationList_AST!=null &&declarationList_AST.getFirstChild()!=null ?
					declarationList_AST.getFirstChild() : declarationList_AST;
				currentAST.advanceChildToEnd();
			}
			declarationList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = declarationList_AST;
	}
	
	public final void declaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST declaration_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LCURLY:
			case LITERAL_enum:
			case DOC:
			case LITERAL_bitmask:
			case LITERAL_union:
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case LITERAL_string:
			case LITERAL_bit:
			case LITERAL_big:
			case LITERAL_little:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				fieldDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				declaration_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_condition:
			{
				conditionDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				declaration_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_const:
			{
				constDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				declaration_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = declaration_AST;
	}
	
/****************************************************************/
	public final void fieldDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldDefinition_AST = null;
		Token  d = null;
		AST d_AST = null;
		AST l_AST = null;
		AST t_AST = null;
		Token  f = null;
		AST f_AST = null;
		AST a_AST = null;
		AST i_AST = null;
		AST o_AST = null;
		AST c_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case DOC:
			{
				d = LT(1);
				d_AST = astFactory.create(d);
				match(DOC);
				break;
			}
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LCURLY:
			case LITERAL_enum:
			case LITERAL_bitmask:
			case LITERAL_union:
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case LITERAL_string:
			case LITERAL_bit:
			case LITERAL_big:
			case LITERAL_little:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			boolean synPredMatched39 = false;
			if (((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2))))) {
				int _m39 = mark();
				synPredMatched39 = true;
				inputState.guessing++;
				try {
					{
					label();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched39 = false;
				}
				rewind(_m39);
inputState.guessing--;
			}
			if ( synPredMatched39 ) {
				label();
				l_AST = (AST)returnAST;
			}
			else if ((_tokenSet_5.member(LA(1))) && (_tokenSet_6.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			typeReference();
			t_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case ID:
			{
				f = LT(1);
				f_AST = astFactory.create(f);
				match(ID);
				break;
			}
			case SEMICOLON:
			case COLON:
			case ASSIGN:
			case LITERAL_if:
			case LBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LBRACKET:
			{
				arrayRange();
				a_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					t_AST = (AST)astFactory.make( (new ASTArray(3)).add((datascript.ast.ArrayType)astFactory.create(ARRAY,"ARRAY","datascript.ast.ArrayType")).add(t_AST).add(a_AST));
				}
				break;
			}
			case SEMICOLON:
			case COLON:
			case ASSIGN:
			case LITERAL_if:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case ASSIGN:
			{
				fieldInitializer();
				i_AST = (AST)returnAST;
				break;
			}
			case SEMICOLON:
			case COLON:
			case LITERAL_if:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LITERAL_if:
			{
				fieldOptionalClause();
				o_AST = (AST)returnAST;
				break;
			}
			case SEMICOLON:
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COLON:
			{
				fieldCondition();
				c_AST = (AST)returnAST;
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				fieldDefinition_AST = (AST)currentAST.root;
				fieldDefinition_AST = (AST)astFactory.make( (new ASTArray(8)).add((datascript.ast.Field)astFactory.create(FIELD,"field","datascript.ast.Field")).add(t_AST).add(f_AST).add(i_AST).add(o_AST).add(c_AST).add(d_AST).add(l_AST));
				currentAST.root = fieldDefinition_AST;
				currentAST.child = fieldDefinition_AST!=null &&fieldDefinition_AST.getFirstChild()!=null ?
					fieldDefinition_AST.getFirstChild() : fieldDefinition_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = fieldDefinition_AST;
	}
	
/******************* begin of condition stuff *****************/
	public final void conditionDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionDefinition_AST = null;
		
		try {      // for error handling
			AST tmp4_AST = null;
			tmp4_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp4_AST);
			match(LITERAL_condition);
			AST tmp5_AST = null;
			tmp5_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp5_AST);
			match(ID);
			parameterList();
			astFactory.addASTChild(currentAST, returnAST);
			conditionBlock();
			astFactory.addASTChild(currentAST, returnAST);
			conditionDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = conditionDefinition_AST;
	}
	
/******************* end of enumerator stuff *****************/
	public final void constDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constDeclaration_AST = null;
		
		try {      // for error handling
			AST tmp6_AST = null;
			tmp6_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp6_AST);
			match(LITERAL_const);
			typeDeclaration();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp7_AST = null;
			tmp7_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp7_AST);
			match(ID);
			match(ASSIGN);
			typeValue();
			astFactory.addASTChild(currentAST, returnAST);
			constDeclaration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = constDeclaration_AST;
	}
	
	public final void label() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST label_AST = null;
		AST g_AST = null;
		AST e_AST = null;
		AST x_AST = null;
		
		try {      // for error handling
			boolean synPredMatched8 = false;
			if (((_tokenSet_3.member(LA(1))) && (_tokenSet_8.member(LA(2))))) {
				int _m8 = mark();
				synPredMatched8 = true;
				inputState.guessing++;
				try {
					{
					globalLabel();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched8 = false;
				}
				rewind(_m8);
inputState.guessing--;
			}
			if ( synPredMatched8 ) {
				globalLabel();
				g_AST = (AST)returnAST;
				expression();
				e_AST = (AST)returnAST;
				AST tmp9_AST = null;
				tmp9_AST = astFactory.create(LT(1));
				match(COLON);
				if ( inputState.guessing==0 ) {
					label_AST = (AST)currentAST.root;
					label_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LABEL,"label")).add(e_AST).add(g_AST));
					currentAST.root = label_AST;
					currentAST.child = label_AST!=null &&label_AST.getFirstChild()!=null ?
						label_AST.getFirstChild() : label_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((_tokenSet_3.member(LA(1))) && (_tokenSet_9.member(LA(2)))) {
				expression();
				x_AST = (AST)returnAST;
				AST tmp10_AST = null;
				tmp10_AST = astFactory.create(LT(1));
				match(COLON);
				if ( inputState.guessing==0 ) {
					label_AST = (AST)currentAST.root;
					label_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LABEL,"label")).add(x_AST));
					currentAST.root = label_AST;
					currentAST.child = label_AST!=null &&label_AST.getFirstChild()!=null ?
						label_AST.getFirstChild() : label_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = label_AST;
	}
	
	public final void globalLabel() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST globalLabel_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(DOUBLECOLON);
			globalLabel_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = globalLabel_AST;
	}
	
/*********************************************************************/
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			assignmentExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop92:
			do {
				if ((LA(1)==COMMA)) {
					AST tmp12_AST = null;
					tmp12_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp12_AST);
					match(COMMA);
					assignmentExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop92;
				}
				
			} while (true);
			}
			expression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_10);
			} else {
			  throw ex;
			}
		}
		returnAST = expression_AST;
	}
	
	public final void parameterList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterList_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			{
			switch ( LA(1)) {
			case ID:
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case LITERAL_string:
			case LITERAL_bit:
			case LITERAL_big:
			case LITERAL_little:
			{
				parameterDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop14:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						parameterDefinition();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop14;
					}
					
				} while (true);
				}
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				parameterList_AST = (AST)currentAST.root;
				parameterList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PARAM,"param")).add(parameterList_AST));
				currentAST.root = parameterList_AST;
				currentAST.child = parameterList_AST!=null &&parameterList_AST.getFirstChild()!=null ?
					parameterList_AST.getFirstChild() : parameterList_AST;
				currentAST.advanceChildToEnd();
			}
			parameterList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_11);
			} else {
			  throw ex;
			}
		}
		returnAST = parameterList_AST;
	}
	
	public final void conditionBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionBlock_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			AST tmp16_AST = null;
			tmp16_AST = astFactory.create(LT(1));
			match(LCURLY);
			{
			_loop17:
			do {
				if ((_tokenSet_3.member(LA(1)))) {
					conditionExpression();
					e_AST = (AST)returnAST;
					match(SEMICOLON);
				}
				else {
					break _loop17;
				}
				
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				conditionBlock_AST = (AST)currentAST.root;
				conditionBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BLOCK,"BLOCK")).add(e_AST));
				currentAST.root = conditionBlock_AST;
				currentAST.child = conditionBlock_AST!=null &&conditionBlock_AST.getFirstChild()!=null ?
					conditionBlock_AST.getFirstChild() : conditionBlock_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_2);
			} else {
			  throw ex;
			}
		}
		returnAST = conditionBlock_AST;
	}
	
	public final void parameterDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterDefinition_AST = null;
		
		try {      // for error handling
			definedType();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp19_AST);
			match(ID);
			parameterDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = parameterDefinition_AST;
	}
	
	public final void conditionExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionExpression_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			conditionExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = conditionExpression_AST;
	}
	
	public final void definedType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST definedType_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				typeSymbol();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					definedType_AST = (AST)currentAST.root;
					definedType_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.TypeReference)astFactory.create(TYPEREF,"","datascript.ast.TypeReference")).add(definedType_AST));
					currentAST.root = definedType_AST;
					currentAST.child = definedType_AST!=null &&definedType_AST.getFirstChild()!=null ?
						definedType_AST.getFirstChild() : definedType_AST;
					currentAST.advanceChildToEnd();
				}
				definedType_AST = (AST)currentAST.root;
				break;
			}
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case LITERAL_string:
			case LITERAL_bit:
			case LITERAL_big:
			case LITERAL_little:
			{
				builtinType();
				astFactory.addASTChild(currentAST, returnAST);
				definedType_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = definedType_AST;
	}
	
/******************* begin of enumerator stuff *****************/
	public final void enumDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enumDeclaration_AST = null;
		
		try {      // for error handling
			datascript.ast.EnumType tmp20_AST = null;
			tmp20_AST = (datascript.ast.EnumType)astFactory.create(LT(1),"datascript.ast.EnumType");
			astFactory.makeASTRoot(currentAST, tmp20_AST);
			match(LITERAL_enum);
			builtinType();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ID:
			{
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp21_AST);
				match(ID);
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			enumMemberList();
			astFactory.addASTChild(currentAST, returnAST);
			enumDeclaration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = enumDeclaration_AST;
	}
	
	public final void builtinType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtinType_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_big:
			case LITERAL_little:
			{
				byteOrderModifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case LITERAL_string:
			case LITERAL_bit:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			builtinTypeDefaultOrder();
			astFactory.addASTChild(currentAST, returnAST);
			builtinType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = builtinType_AST;
	}
	
	public final void enumMemberList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enumMemberList_AST = null;
		
		try {      // for error handling
			match(LCURLY);
			enumItem();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop24:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					enumItem();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop24;
				}
				
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				enumMemberList_AST = (AST)currentAST.root;
				enumMemberList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MEMBERS)).add(enumMemberList_AST));
				currentAST.root = enumMemberList_AST;
				currentAST.child = enumMemberList_AST!=null &&enumMemberList_AST.getFirstChild()!=null ?
					enumMemberList_AST.getFirstChild() : enumMemberList_AST;
				currentAST.advanceChildToEnd();
			}
			enumMemberList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = enumMemberList_AST;
	}
	
	public final void enumItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST enumItem_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case DOC:
			{
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(DOC);
				break;
			}
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp26_AST = null;
			tmp26_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp26_AST);
			match(ID);
			{
			switch ( LA(1)) {
			case ASSIGN:
			{
				match(ASSIGN);
				constantExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COMMA:
			case RCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				enumItem_AST = (AST)currentAST.root;
				enumItem_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.EnumItem)astFactory.create(ITEM,"","datascript.ast.EnumItem")).add(enumItem_AST));
				currentAST.root = enumItem_AST;
				currentAST.child = enumItem_AST!=null &&enumItem_AST.getFirstChild()!=null ?
					enumItem_AST.getFirstChild() : enumItem_AST;
				currentAST.advanceChildToEnd();
			}
			enumItem_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_16);
			} else {
			  throw ex;
			}
		}
		returnAST = enumItem_AST;
	}
	
	public final void constantExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constantExpression_AST = null;
		
		try {      // for error handling
			conditionalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			constantExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = constantExpression_AST;
	}
	
	public final void bitmaskDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bitmaskDeclaration_AST = null;
		
		try {      // for error handling
			AST tmp28_AST = null;
			tmp28_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp28_AST);
			match(LITERAL_bitmask);
			builtinType();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ID:
			{
				AST tmp29_AST = null;
				tmp29_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp29_AST);
				match(ID);
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			enumMemberList();
			astFactory.addASTChild(currentAST, returnAST);
			bitmaskDeclaration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = bitmaskDeclaration_AST;
	}
	
	public final void typeDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeDeclaration_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_enum:
			{
				enumDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				typeDeclaration_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bitmask:
			{
				bitmaskDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				typeDeclaration_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched58 = false;
				if (((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2))))) {
					int _m58 = mark();
					synPredMatched58 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case LITERAL_big:
						case LITERAL_little:
						{
							byteOrderModifier();
							break;
						}
						case LPAREN:
						case ID:
						case LCURLY:
						case LITERAL_union:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case LITERAL_union:
						{
							match(LITERAL_union);
							break;
						}
						case LPAREN:
						case ID:
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case ID:
						{
							match(ID);
							break;
						}
						case LPAREN:
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case LPAREN:
						{
							match(LPAREN);
							parameterDefinition();
							break;
						}
						case LCURLY:
						{
							match(LCURLY);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched58 = false;
					}
					rewind(_m58);
inputState.guessing--;
				}
				if ( synPredMatched58 ) {
					structDeclaration();
					astFactory.addASTChild(currentAST, returnAST);
					typeDeclaration_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_20.member(LA(1))) && (_tokenSet_21.member(LA(2)))) {
					definedType();
					astFactory.addASTChild(currentAST, returnAST);
					typeDeclaration_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_22);
			} else {
			  throw ex;
			}
		}
		returnAST = typeDeclaration_AST;
	}
	
	public final void typeValue() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeValue_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				constantExpression();
				astFactory.addASTChild(currentAST, returnAST);
				typeValue_AST = (AST)currentAST.root;
				break;
			}
			case LCURLY:
			{
				AST tmp30_AST = null;
				tmp30_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp30_AST);
				match(LCURLY);
				typeValueList();
				astFactory.addASTChild(currentAST, returnAST);
				match(RCURLY);
				typeValue_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_17);
			} else {
			  throw ex;
			}
		}
		returnAST = typeValue_AST;
	}
	
	public final void typeValueList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeValueList_AST = null;
		
		try {      // for error handling
			typeValue();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop34:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					typeValue();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop34;
				}
				
			} while (true);
			}
			typeValueList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_23);
			} else {
			  throw ex;
			}
		}
		returnAST = typeValueList_AST;
	}
	
	public final void typeReference() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeReference_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_enum:
			{
				enumDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				typeReference_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bitmask:
			{
				bitmaskDeclaration();
				astFactory.addASTChild(currentAST, returnAST);
				typeReference_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched65 = false;
				if (((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2))))) {
					int _m65 = mark();
					synPredMatched65 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case LITERAL_big:
						case LITERAL_little:
						{
							byteOrderModifier();
							break;
						}
						case LPAREN:
						case ID:
						case LCURLY:
						case LITERAL_union:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case LITERAL_union:
						{
							match(LITERAL_union);
							break;
						}
						case LPAREN:
						case ID:
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case ID:
						{
							match(ID);
							break;
						}
						case LPAREN:
						case LCURLY:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						{
						switch ( LA(1)) {
						case LPAREN:
						{
							match(LPAREN);
							parameterDefinition();
							break;
						}
						case LCURLY:
						{
							match(LCURLY);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched65 = false;
					}
					rewind(_m65);
inputState.guessing--;
				}
				if ( synPredMatched65 ) {
					structDeclaration();
					astFactory.addASTChild(currentAST, returnAST);
					typeReference_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched67 = false;
					if (((_tokenSet_20.member(LA(1))) && (_tokenSet_24.member(LA(2))))) {
						int _m67 = mark();
						synPredMatched67 = true;
						inputState.guessing++;
						try {
							{
							match(ID);
							match(LPAREN);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched67 = false;
						}
						rewind(_m67);
inputState.guessing--;
					}
					if ( synPredMatched67 ) {
						paramTypeInstantiation();
						astFactory.addASTChild(currentAST, returnAST);
						typeReference_AST = (AST)currentAST.root;
					}
					else if ((_tokenSet_20.member(LA(1))) && (_tokenSet_25.member(LA(2)))) {
						definedType();
						astFactory.addASTChild(currentAST, returnAST);
						typeReference_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex);
					recover(ex,_tokenSet_14);
				} else {
				  throw ex;
				}
			}
			returnAST = typeReference_AST;
		}
		
	public final void arrayRange() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayRange_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			match(LBRACKET);
			{
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				rangeExpression();
				r_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RBRACKET);
			arrayRange_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_26);
			} else {
			  throw ex;
			}
		}
		returnAST = arrayRange_AST;
	}
	
	public final void fieldInitializer() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldInitializer_AST = null;
		
		try {      // for error handling
			AST tmp35_AST = null;
			tmp35_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp35_AST);
			match(ASSIGN);
			typeValue();
			astFactory.addASTChild(currentAST, returnAST);
			fieldInitializer_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_27);
			} else {
			  throw ex;
			}
		}
		returnAST = fieldInitializer_AST;
	}
	
	public final void fieldOptionalClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldOptionalClause_AST = null;
		
		try {      // for error handling
			AST tmp36_AST = null;
			tmp36_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp36_AST);
			match(LITERAL_if);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			fieldOptionalClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_28);
			} else {
			  throw ex;
			}
		}
		returnAST = fieldOptionalClause_AST;
	}
	
	public final void fieldCondition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldCondition_AST = null;
		
		try {      // for error handling
			AST tmp37_AST = null;
			tmp37_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp37_AST);
			match(COLON);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			fieldCondition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_7);
			} else {
			  throw ex;
			}
		}
		returnAST = fieldCondition_AST;
	}
	
	public final void typeArgumentList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeArgumentList_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			{
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				functionArgument();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop48:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						functionArgument();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop48;
					}
					
				} while (true);
				}
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			typeArgumentList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = typeArgumentList_AST;
	}
	
	public final void functionArgument() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionArgument_AST = null;
		
		try {      // for error handling
			assignmentExpression();
			astFactory.addASTChild(currentAST, returnAST);
			functionArgument_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_12);
			} else {
			  throw ex;
			}
		}
		returnAST = functionArgument_AST;
	}
	
	public final void byteOrderModifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST byteOrderModifier_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_big:
			{
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp41_AST);
				match(LITERAL_big);
				byteOrderModifier_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_little:
			{
				AST tmp42_AST = null;
				tmp42_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp42_AST);
				match(LITERAL_little);
				byteOrderModifier_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_29);
			} else {
			  throw ex;
			}
		}
		returnAST = byteOrderModifier_AST;
	}
	
	public final void structDeclaration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST structDeclaration_AST = null;
		Token  u = null;
		AST u_AST = null;
		Token  n = null;
		AST n_AST = null;
		AST p_AST = null;
		AST m_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_big:
			case LITERAL_little:
			{
				byteOrderModifier();
				break;
			}
			case LPAREN:
			case ID:
			case LCURLY:
			case LITERAL_union:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LITERAL_union:
			{
				u = LT(1);
				u_AST = astFactory.create(u);
				match(LITERAL_union);
				break;
			}
			case LPAREN:
			case ID:
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case ID:
			{
				n = LT(1);
				n_AST = astFactory.create(n);
				match(ID);
				break;
			}
			case LPAREN:
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				parameterList();
				p_AST = (AST)returnAST;
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			memberList();
			m_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				structDeclaration_AST = (AST)currentAST.root;
				if (u == null)
				{
				structDeclaration_AST = (AST)astFactory.make( (new ASTArray(4)).add((datascript.ast.StructType)astFactory.create(STRUCT,"struct","datascript.ast.StructType")).add(n_AST).add(p_AST).add(m_AST)); 
				}
				else
				{
				structDeclaration_AST = (AST)astFactory.make( (new ASTArray(4)).add((datascript.ast.UnionType)astFactory.create(UNION,"union","datascript.ast.UnionType")).add(n_AST).add(p_AST).add(m_AST)); 
				}
				
				currentAST.root = structDeclaration_AST;
				currentAST.child = structDeclaration_AST!=null &&structDeclaration_AST.getFirstChild()!=null ?
					structDeclaration_AST.getFirstChild() : structDeclaration_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = structDeclaration_AST;
	}
	
	public final void paramTypeInstantiation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST paramTypeInstantiation_AST = null;
		
		try {      // for error handling
			definedType();
			astFactory.addASTChild(currentAST, returnAST);
			typeArgumentList();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				paramTypeInstantiation_AST = (AST)currentAST.root;
				paramTypeInstantiation_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.TypeInstantiation)astFactory.create(INST,"INST","datascript.ast.TypeInstantiation")).add(paramTypeInstantiation_AST));
				currentAST.root = paramTypeInstantiation_AST;
				currentAST.child = paramTypeInstantiation_AST!=null &&paramTypeInstantiation_AST.getFirstChild()!=null ?
					paramTypeInstantiation_AST.getFirstChild() : paramTypeInstantiation_AST;
				currentAST.advanceChildToEnd();
			}
			paramTypeInstantiation_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = paramTypeInstantiation_AST;
	}
	
	public final void memberList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST memberList_AST = null;
		
		try {      // for error handling
			match(LCURLY);
			{
			_loop76:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					declaration();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop76;
				}
				
			} while (true);
			}
			match(RCURLY);
			if ( inputState.guessing==0 ) {
				memberList_AST = (AST)currentAST.root;
				memberList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MEMBERS)).add(memberList_AST));
				currentAST.root = memberList_AST;
				currentAST.child = memberList_AST!=null &&memberList_AST.getFirstChild()!=null ?
					memberList_AST.getFirstChild() : memberList_AST;
				currentAST.advanceChildToEnd();
			}
			memberList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_14);
			} else {
			  throw ex;
			}
		}
		returnAST = memberList_AST;
	}
	
	public final void typeSymbol() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSymbol_AST = null;
		
		try {      // for error handling
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp45_AST);
			match(ID);
			{
			_loop80:
			do {
				if ((LA(1)==DOT)) {
					dotOperand();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop80;
				}
				
			} while (true);
			}
			typeSymbol_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_13);
			} else {
			  throw ex;
			}
		}
		returnAST = typeSymbol_AST;
	}
	
	public final void dotOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dotOperand_AST = null;
		
		try {      // for error handling
			datascript.ast.Expression tmp46_AST = null;
			tmp46_AST = (datascript.ast.Expression)astFactory.create(LT(1),"datascript.ast.Expression");
			astFactory.makeASTRoot(currentAST, tmp46_AST);
			match(DOT);
			datascript.ast.Expression tmp47_AST = null;
			tmp47_AST = (datascript.ast.Expression)astFactory.create(LT(1),"datascript.ast.Expression");
			astFactory.addASTChild(currentAST, tmp47_AST);
			match(ID);
			dotOperand_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_30);
			} else {
			  throw ex;
			}
		}
		returnAST = dotOperand_AST;
	}
	
	public final void builtinTypeDefaultOrder() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtinTypeDefaultOrder_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case 68:
			case 69:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			{
				integerType();
				astFactory.addASTChild(currentAST, returnAST);
				builtinTypeDefaultOrder_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_string:
			{
				AST tmp48_AST = null;
				tmp48_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp48_AST);
				match(LITERAL_string);
				builtinTypeDefaultOrder_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_bit:
			{
				bitField();
				astFactory.addASTChild(currentAST, returnAST);
				builtinTypeDefaultOrder_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = builtinTypeDefaultOrder_AST;
	}
	
	public final void integerType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST integerType_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case 68:
			{
				match(68);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(UINT8,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 69:
			{
				match(69);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(UINT16,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 70:
			{
				match(70);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(UINT32,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 71:
			{
				match(71);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(UINT64,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 72:
			{
				match(72);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(INT8,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 73:
			{
				match(73);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(INT16,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 74:
			{
				match(74);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(INT32,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case 75:
			{
				match(75);
				if ( inputState.guessing==0 ) {
					integerType_AST = (AST)currentAST.root;
					integerType_AST = (datascript.ast.StdIntegerType)astFactory.create(INT64,"","datascript.ast.StdIntegerType");
					currentAST.root = integerType_AST;
					currentAST.child = integerType_AST!=null &&integerType_AST.getFirstChild()!=null ?
						integerType_AST.getFirstChild() : integerType_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = integerType_AST;
	}
	
	public final void bitField() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST bitField_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			match(LITERAL_bit);
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				datascript.ast.IntegerExpression tmp59_AST = null;
				tmp59_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.addASTChild(currentAST, tmp59_AST);
				match(INTEGER_LITERAL);
				break;
			}
			case LT:
			{
				match(LT);
				shiftExpression();
				e_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				match(GT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				bitField_AST = (AST)currentAST.root;
				bitField_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.BitFieldType)astFactory.create(BIT,"","datascript.ast.BitFieldType")).add(bitField_AST));
				currentAST.root = bitField_AST;
				currentAST.child = bitField_AST!=null &&bitField_AST.getFirstChild()!=null ?
					bitField_AST.getFirstChild() : bitField_AST;
				currentAST.advanceChildToEnd();
			}
			bitField_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_15);
			} else {
			  throw ex;
			}
		}
		returnAST = bitField_AST;
	}
	
	public final void shiftExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST shiftExpression_AST = null;
		
		try {      // for error handling
			additiveExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop125:
			do {
				if ((LA(1)==LSHIFT||LA(1)==RSHIFT)) {
					{
					switch ( LA(1)) {
					case LSHIFT:
					{
						datascript.ast.IntegerExpression tmp62_AST = null;
						tmp62_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp62_AST);
						match(LSHIFT);
						break;
					}
					case RSHIFT:
					{
						datascript.ast.IntegerExpression tmp63_AST = null;
						tmp63_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp63_AST);
						match(RSHIFT);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					additiveExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop125;
				}
				
			} while (true);
			}
			shiftExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_31);
			} else {
			  throw ex;
			}
		}
		returnAST = shiftExpression_AST;
	}
	
	public final void rangeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rangeExpression_AST = null;
		
		try {      // for error handling
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case RANGE:
			{
				match(RANGE);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case RBRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rangeExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_32);
			} else {
			  throw ex;
			}
		}
		returnAST = rangeExpression_AST;
	}
	
	public final void assignmentExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentExpression_AST = null;
		AST l_AST = null;
		AST op_AST = null;
		AST r_AST = null;
		
		try {      // for error handling
			boolean synPredMatched95 = false;
			if (((_tokenSet_33.member(LA(1))) && (_tokenSet_34.member(LA(2))))) {
				int _m95 = mark();
				synPredMatched95 = true;
				inputState.guessing++;
				try {
					{
					unaryExpression();
					assignmentOperator();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched95 = false;
				}
				rewind(_m95);
inputState.guessing--;
			}
			if ( synPredMatched95 ) {
				unaryExpression();
				l_AST = (AST)returnAST;
				assignmentOperator();
				op_AST = (AST)returnAST;
				assignmentExpression();
				r_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					assignmentExpression_AST = (AST)currentAST.root;
					assignmentExpression_AST = (AST)astFactory.make( (new ASTArray(3)).add(op_AST).add(l_AST).add(r_AST));
					currentAST.root = assignmentExpression_AST;
					currentAST.child = assignmentExpression_AST!=null &&assignmentExpression_AST.getFirstChild()!=null ?
						assignmentExpression_AST.getFirstChild() : assignmentExpression_AST;
					currentAST.advanceChildToEnd();
				}
			}
			else if ((_tokenSet_3.member(LA(1))) && (_tokenSet_35.member(LA(2)))) {
				quantifiedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				assignmentExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_36);
			} else {
			  throw ex;
			}
		}
		returnAST = assignmentExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case LPAREN:
			case ID:
			case STRING_LITERAL:
			{
				postfixExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			{
				unaryOperand();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_sizeof:
			{
				sizeOfOperand();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_lengthof:
			{
				lengthOfOperand();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void assignmentOperator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentOperator_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ASSIGN:
			{
				AST tmp65_AST = null;
				tmp65_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp65_AST);
				match(ASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case MULTASSIGN:
			{
				AST tmp66_AST = null;
				tmp66_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp66_AST);
				match(MULTASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case DIVASSIGN:
			{
				AST tmp67_AST = null;
				tmp67_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp67_AST);
				match(DIVASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case MODASSIGN:
			{
				AST tmp68_AST = null;
				tmp68_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp68_AST);
				match(MODASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case PLUSASSIGN:
			{
				AST tmp69_AST = null;
				tmp69_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp69_AST);
				match(PLUSASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case MINUSASSIGN:
			{
				AST tmp70_AST = null;
				tmp70_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp70_AST);
				match(MINUSASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case LSHIFTASSIGN:
			{
				AST tmp71_AST = null;
				tmp71_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp71_AST);
				match(LSHIFTASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case RSHIFTASSIGN:
			{
				AST tmp72_AST = null;
				tmp72_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp72_AST);
				match(RSHIFTASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case ANDASSIGN:
			{
				AST tmp73_AST = null;
				tmp73_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp73_AST);
				match(ANDASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case XORASSIGN:
			{
				AST tmp74_AST = null;
				tmp74_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp74_AST);
				match(XORASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			case ORASSIGN:
			{
				AST tmp75_AST = null;
				tmp75_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp75_AST);
				match(ORASSIGN);
				assignmentOperator_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_3);
			} else {
			  throw ex;
			}
		}
		returnAST = assignmentOperator_AST;
	}
	
	public final void quantifiedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST quantifiedExpression_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_forall:
			{
				quantifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			conditionalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			quantifiedExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_36);
			} else {
			  throw ex;
			}
		}
		returnAST = quantifiedExpression_AST;
	}
	
	public final void quantifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST quantifier_AST = null;
		
		try {      // for error handling
			AST tmp76_AST = null;
			tmp76_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp76_AST);
			match(LITERAL_forall);
			AST tmp77_AST = null;
			tmp77_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp77_AST);
			match(ID);
			match(LITERAL_in);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(COLON);
			quantifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
		returnAST = quantifier_AST;
	}
	
	public final void conditionalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionalExpression_AST = null;
		
		try {      // for error handling
			logicalOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case QUESTIONMARK:
			{
				datascript.ast.Expression tmp80_AST = null;
				tmp80_AST = (datascript.ast.Expression)astFactory.create(LT(1),"datascript.ast.Expression");
				astFactory.makeASTRoot(currentAST, tmp80_AST);
				match(QUESTIONMARK);
				expression();
				astFactory.addASTChild(currentAST, returnAST);
				match(COLON);
				conditionalExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			conditionalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_38);
			} else {
			  throw ex;
			}
		}
		returnAST = conditionalExpression_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		try {      // for error handling
			logicalAndExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LOGICALOR:
			{
				datascript.ast.BooleanExpression tmp82_AST = null;
				tmp82_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
				astFactory.makeASTRoot(currentAST, tmp82_AST);
				match(LOGICALOR);
				logicalOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			logicalOrExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_39);
			} else {
			  throw ex;
			}
		}
		returnAST = logicalOrExpression_AST;
	}
	
	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;
		
		try {      // for error handling
			inclusiveOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LOGICALAND:
			{
				datascript.ast.BooleanExpression tmp83_AST = null;
				tmp83_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
				astFactory.makeASTRoot(currentAST, tmp83_AST);
				match(LOGICALAND);
				logicalAndExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LOGICALOR:
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			logicalAndExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_40);
			} else {
			  throw ex;
			}
		}
		returnAST = logicalAndExpression_AST;
	}
	
	public final void inclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inclusiveOrExpression_AST = null;
		
		try {      // for error handling
			exclusiveOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OR:
			{
				datascript.ast.IntegerExpression tmp84_AST = null;
				tmp84_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.makeASTRoot(currentAST, tmp84_AST);
				match(OR);
				inclusiveOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LOGICALOR:
			case LOGICALAND:
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			inclusiveOrExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_41);
			} else {
			  throw ex;
			}
		}
		returnAST = inclusiveOrExpression_AST;
	}
	
	public final void exclusiveOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exclusiveOrExpression_AST = null;
		
		try {      // for error handling
			andExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case XOR:
			{
				datascript.ast.IntegerExpression tmp85_AST = null;
				tmp85_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.makeASTRoot(currentAST, tmp85_AST);
				match(XOR);
				exclusiveOrExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OR:
			case LOGICALOR:
			case LOGICALAND:
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exclusiveOrExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_42);
			} else {
			  throw ex;
			}
		}
		returnAST = exclusiveOrExpression_AST;
	}
	
	public final void andExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpression_AST = null;
		
		try {      // for error handling
			equalityExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case AND:
			{
				datascript.ast.IntegerExpression tmp86_AST = null;
				tmp86_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.makeASTRoot(currentAST, tmp86_AST);
				match(AND);
				andExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OR:
			case XOR:
			case LOGICALOR:
			case LOGICALAND:
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			andExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_43);
			} else {
			  throw ex;
			}
		}
		returnAST = andExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		
		try {      // for error handling
			relationalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQ:
			case NE:
			{
				{
				switch ( LA(1)) {
				case EQ:
				{
					datascript.ast.BooleanExpression tmp87_AST = null;
					tmp87_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
					astFactory.makeASTRoot(currentAST, tmp87_AST);
					match(EQ);
					break;
				}
				case NE:
				{
					datascript.ast.BooleanExpression tmp88_AST = null;
					tmp88_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
					astFactory.makeASTRoot(currentAST, tmp88_AST);
					match(NE);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				equalityExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OR:
			case XOR:
			case AND:
			case LOGICALOR:
			case LOGICALAND:
			case QUESTIONMARK:
			case SEMICOLON:
			case COLON:
			case DOUBLECOLON:
			case COMMA:
			case RPAREN:
			case RCURLY:
			case LITERAL_if:
			case RBRACKET:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			equalityExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_44);
			} else {
			  throw ex;
			}
		}
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		
		try {      // for error handling
			shiftExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop121:
			do {
				if (((LA(1) >= LT && LA(1) <= GT))) {
					{
					switch ( LA(1)) {
					case LT:
					{
						datascript.ast.BooleanExpression tmp89_AST = null;
						tmp89_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
						astFactory.makeASTRoot(currentAST, tmp89_AST);
						match(LT);
						break;
					}
					case LE:
					{
						datascript.ast.BooleanExpression tmp90_AST = null;
						tmp90_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
						astFactory.makeASTRoot(currentAST, tmp90_AST);
						match(LE);
						break;
					}
					case GT:
					{
						datascript.ast.BooleanExpression tmp91_AST = null;
						tmp91_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
						astFactory.makeASTRoot(currentAST, tmp91_AST);
						match(GT);
						break;
					}
					case GE:
					{
						datascript.ast.BooleanExpression tmp92_AST = null;
						tmp92_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
						astFactory.makeASTRoot(currentAST, tmp92_AST);
						match(GE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					shiftExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop121;
				}
				
			} while (true);
			}
			relationalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_45);
			} else {
			  throw ex;
			}
		}
		returnAST = relationalExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		try {      // for error handling
			multiplicativeExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop129:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					switch ( LA(1)) {
					case PLUS:
					{
						datascript.ast.IntegerExpression tmp93_AST = null;
						tmp93_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp93_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						datascript.ast.IntegerExpression tmp94_AST = null;
						tmp94_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp94_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					multiplicativeExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop129;
				}
				
			} while (true);
			}
			additiveExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_46);
			} else {
			  throw ex;
			}
		}
		returnAST = additiveExpression_AST;
	}
	
	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicativeExpression_AST = null;
		
		try {      // for error handling
			castExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop133:
			do {
				if (((LA(1) >= MULTIPLY && LA(1) <= MODULO))) {
					{
					switch ( LA(1)) {
					case MULTIPLY:
					{
						datascript.ast.IntegerExpression tmp95_AST = null;
						tmp95_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp95_AST);
						match(MULTIPLY);
						break;
					}
					case DIVIDE:
					{
						datascript.ast.IntegerExpression tmp96_AST = null;
						tmp96_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp96_AST);
						match(DIVIDE);
						break;
					}
					case MODULO:
					{
						datascript.ast.IntegerExpression tmp97_AST = null;
						tmp97_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
						astFactory.makeASTRoot(currentAST, tmp97_AST);
						match(MODULO);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					castExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop133;
				}
				
			} while (true);
			}
			multiplicativeExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_47);
			} else {
			  throw ex;
			}
		}
		returnAST = multiplicativeExpression_AST;
	}
	
	public final void castExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST castExpression_AST = null;
		
		try {      // for error handling
			boolean synPredMatched136 = false;
			if (((LA(1)==LPAREN) && (_tokenSet_20.member(LA(2))))) {
				int _m136 = mark();
				synPredMatched136 = true;
				inputState.guessing++;
				try {
					{
					castOperand();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched136 = false;
				}
				rewind(_m136);
inputState.guessing--;
			}
			if ( synPredMatched136 ) {
				castOperand();
				astFactory.addASTChild(currentAST, returnAST);
				castExpression_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_33.member(LA(1))) && (_tokenSet_48.member(LA(2)))) {
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				castExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = castExpression_AST;
	}
	
	public final void castOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST castOperand_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			definedType();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			castExpression();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				castOperand_AST = (AST)currentAST.root;
				castOperand_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CAST,"CAST")).add(castOperand_AST));
				currentAST.root = castOperand_AST;
				currentAST.child = castOperand_AST!=null &&castOperand_AST.getFirstChild()!=null ?
					castOperand_AST.getFirstChild() : castOperand_AST;
				currentAST.advanceChildToEnd();
			}
			castOperand_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = castOperand_AST;
	}
	
	public final void postfixExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixExpression_AST = null;
		AST e_AST = null;
		AST o_AST = null;
		
		try {      // for error handling
			primaryExpression();
			e_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				postfixExpression_AST = (AST)currentAST.root;
				postfixExpression_AST = e_AST;
				currentAST.root = postfixExpression_AST;
				currentAST.child = postfixExpression_AST!=null &&postfixExpression_AST.getFirstChild()!=null ?
					postfixExpression_AST.getFirstChild() : postfixExpression_AST;
				currentAST.advanceChildToEnd();
			}
			{
			_loop145:
			do {
				if ((_tokenSet_49.member(LA(1)))) {
					postfixOperand();
					o_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						postfixExpression_AST = (AST)currentAST.root;
						AST rhs = o_AST.getFirstChild(); 
						postfixExpression_AST= (AST)astFactory.make( (new ASTArray(3)).add(o_AST).add(postfixExpression_AST).add(rhs));
						currentAST.root = postfixExpression_AST;
						currentAST.child = postfixExpression_AST!=null &&postfixExpression_AST.getFirstChild()!=null ?
							postfixExpression_AST.getFirstChild() : postfixExpression_AST;
						currentAST.advanceChildToEnd();
					}
				}
				else {
					break _loop145;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = postfixExpression_AST;
	}
	
	public final void unaryOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryOperand_AST = null;
		AST o_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			unaryOperator();
			o_AST = (AST)returnAST;
			castExpression();
			e_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				unaryOperand_AST = (AST)currentAST.root;
				unaryOperand_AST = (AST)astFactory.make( (new ASTArray(2)).add(o_AST).add(e_AST));
				currentAST.root = unaryOperand_AST;
				currentAST.child = unaryOperand_AST!=null &&unaryOperand_AST.getFirstChild()!=null ?
					unaryOperand_AST.getFirstChild() : unaryOperand_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = unaryOperand_AST;
	}
	
	public final void sizeOfOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sizeOfOperand_AST = null;
		
		try {      // for error handling
			AST tmp100_AST = null;
			tmp100_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp100_AST);
			match(LITERAL_sizeof);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			sizeOfOperand_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = sizeOfOperand_AST;
	}
	
	public final void lengthOfOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lengthOfOperand_AST = null;
		
		try {      // for error handling
			AST tmp101_AST = null;
			tmp101_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp101_AST);
			match(LITERAL_lengthof);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			lengthOfOperand_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_37);
			} else {
			  throw ex;
			}
		}
		returnAST = lengthOfOperand_AST;
	}
	
	public final void unaryOperator() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryOperator_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PLUS:
			{
				match(PLUS);
				if ( inputState.guessing==0 ) {
					unaryOperator_AST = (AST)currentAST.root;
					unaryOperator_AST = (datascript.ast.IntegerExpression)astFactory.create(UPLUS,"","datascript.ast.IntegerExpression");
					currentAST.root = unaryOperator_AST;
					currentAST.child = unaryOperator_AST!=null &&unaryOperator_AST.getFirstChild()!=null ?
						unaryOperator_AST.getFirstChild() : unaryOperator_AST;
					currentAST.advanceChildToEnd();
				}
				unaryOperator_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			{
				match(MINUS);
				if ( inputState.guessing==0 ) {
					unaryOperator_AST = (AST)currentAST.root;
					unaryOperator_AST = (datascript.ast.IntegerExpression)astFactory.create(UMINUS,"","datascript.ast.IntegerExpression");
					currentAST.root = unaryOperator_AST;
					currentAST.child = unaryOperator_AST!=null &&unaryOperator_AST.getFirstChild()!=null ?
						unaryOperator_AST.getFirstChild() : unaryOperator_AST;
					currentAST.advanceChildToEnd();
				}
				unaryOperator_AST = (AST)currentAST.root;
				break;
			}
			case TILDE:
			{
				datascript.ast.IntegerExpression tmp104_AST = null;
				tmp104_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.addASTChild(currentAST, tmp104_AST);
				match(TILDE);
				unaryOperator_AST = (AST)currentAST.root;
				break;
			}
			case BANG:
			{
				datascript.ast.BooleanExpression tmp105_AST = null;
				tmp105_AST = (datascript.ast.BooleanExpression)astFactory.create(LT(1),"datascript.ast.BooleanExpression");
				astFactory.addASTChild(currentAST, tmp105_AST);
				match(BANG);
				unaryOperator_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_33);
			} else {
			  throw ex;
			}
		}
		returnAST = unaryOperator_AST;
	}
	
	public final void primaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				variableName();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case INTEGER_LITERAL:
			case STRING_LITERAL:
			{
				constant();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case LPAREN:
			{
				parenthesizedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = primaryExpression_AST;
	}
	
	public final void postfixOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST postfixOperand_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LBRACKET:
			{
				arrayOperand();
				astFactory.addASTChild(currentAST, returnAST);
				postfixOperand_AST = (AST)currentAST.root;
				break;
			}
			case LPAREN:
			{
				functionArgumentList();
				astFactory.addASTChild(currentAST, returnAST);
				postfixOperand_AST = (AST)currentAST.root;
				break;
			}
			case DOT:
			{
				dotOperand();
				astFactory.addASTChild(currentAST, returnAST);
				postfixOperand_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_is:
			{
				choiceOperand();
				astFactory.addASTChild(currentAST, returnAST);
				postfixOperand_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = postfixOperand_AST;
	}
	
	public final void arrayOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayOperand_AST = null;
		AST e_AST = null;
		
		try {      // for error handling
			AST tmp106_AST = null;
			tmp106_AST = astFactory.create(LT(1));
			match(LBRACKET);
			expression();
			e_AST = (AST)returnAST;
			AST tmp107_AST = null;
			tmp107_AST = astFactory.create(LT(1));
			match(RBRACKET);
			if ( inputState.guessing==0 ) {
				arrayOperand_AST = (AST)currentAST.root;
				arrayOperand_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.Expression)astFactory.create(ARRAYELEM,"ARRAYELEM","datascript.ast.Expression")).add(e_AST));
				currentAST.root = arrayOperand_AST;
				currentAST.child = arrayOperand_AST!=null &&arrayOperand_AST.getFirstChild()!=null ?
					arrayOperand_AST.getFirstChild() : arrayOperand_AST;
				currentAST.advanceChildToEnd();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = arrayOperand_AST;
	}
	
	public final void functionArgumentList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionArgumentList_AST = null;
		
		try {      // for error handling
			match(LPAREN);
			{
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case TILDE:
			case BANG:
			case LPAREN:
			case ID:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case STRING_LITERAL:
			{
				functionArgument();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop152:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						functionArgument();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop152;
					}
					
				} while (true);
				}
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(RPAREN);
			if ( inputState.guessing==0 ) {
				functionArgumentList_AST = (AST)currentAST.root;
				functionArgumentList_AST = (AST)astFactory.make( (new ASTArray(2)).add((datascript.ast.TypeInstantiation)astFactory.create(INST,"INST","datascript.ast.TypeInstantiation")).add(functionArgumentList_AST));
				currentAST.root = functionArgumentList_AST;
				currentAST.child = functionArgumentList_AST!=null &&functionArgumentList_AST.getFirstChild()!=null ?
					functionArgumentList_AST.getFirstChild() : functionArgumentList_AST;
				currentAST.advanceChildToEnd();
			}
			functionArgumentList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = functionArgumentList_AST;
	}
	
	public final void choiceOperand() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST choiceOperand_AST = null;
		
		try {      // for error handling
			AST tmp111_AST = null;
			tmp111_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp111_AST);
			match(LITERAL_is);
			AST tmp112_AST = null;
			tmp112_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp112_AST);
			match(ID);
			choiceOperand_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = choiceOperand_AST;
	}
	
	public final void variableName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST variableName_AST = null;
		
		try {      // for error handling
			datascript.ast.Expression tmp113_AST = null;
			tmp113_AST = (datascript.ast.Expression)astFactory.create(LT(1),"datascript.ast.Expression");
			astFactory.addASTChild(currentAST, tmp113_AST);
			match(ID);
			variableName_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = variableName_AST;
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			{
				datascript.ast.IntegerExpression tmp114_AST = null;
				tmp114_AST = (datascript.ast.IntegerExpression)astFactory.create(LT(1),"datascript.ast.IntegerExpression");
				astFactory.addASTChild(currentAST, tmp114_AST);
				match(INTEGER_LITERAL);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case STRING_LITERAL:
			{
				AST tmp115_AST = null;
				tmp115_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp115_AST);
				match(STRING_LITERAL);
				constant_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = constant_AST;
	}
	
	public final void parenthesizedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parenthesizedExpression_AST = null;
		
		try {      // for error handling
			datascript.ast.Expression tmp116_AST = null;
			tmp116_AST = (datascript.ast.Expression)astFactory.create(LT(1),"datascript.ast.Expression");
			astFactory.makeASTRoot(currentAST, tmp116_AST);
			match(LPAREN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(RPAREN);
			parenthesizedExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_50);
			} else {
			  throw ex;
			}
		}
		returnAST = parenthesizedExpression_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"FIELD",
		"STRUCT",
		"UNION",
		"PARAM",
		"INST",
		"ARRAY",
		"LABEL",
		"BLOCK",
		"CAST",
		"MEMBERS",
		"TYPEREF",
		"UPLUS",
		"UMINUS",
		"ITEM",
		"BIT",
		"UINT8",
		"UINT16",
		"UINT32",
		"UINT64",
		"INT8",
		"INT16",
		"INT32",
		"INT64",
		"ARRAYELEM",
		"INTEGER_LITERAL",
		"PLUS",
		"MINUS",
		"MULTIPLY",
		"DIVIDE",
		"MODULO",
		"LSHIFT",
		"RSHIFT",
		"OR",
		"XOR",
		"AND",
		"TILDE",
		"EQ",
		"NE",
		"LT",
		"LE",
		"GE",
		"GT",
		"LOGICALOR",
		"LOGICALAND",
		"BANG",
		"QUESTIONMARK",
		"DOT",
		"LPAREN",
		"SEMICOLON",
		"COLON",
		"DOUBLECOLON",
		"\"condition\"",
		"ID",
		"COMMA",
		"RPAREN",
		"LCURLY",
		"RCURLY",
		"\"enum\"",
		"DOC",
		"ASSIGN",
		"\"bitmask\"",
		"\"const\"",
		"\"if\"",
		"\"union\"",
		"\"uint8\"",
		"\"uint16\"",
		"\"uint32\"",
		"\"uint64\"",
		"\"int8\"",
		"\"int16\"",
		"\"int32\"",
		"\"int64\"",
		"\"string\"",
		"\"bit\"",
		"\"big\"",
		"\"little\"",
		"LBRACKET",
		"RBRACKET",
		"MULTASSIGN",
		"DIVASSIGN",
		"MODASSIGN",
		"PLUSASSIGN",
		"MINUSASSIGN",
		"LSHIFTASSIGN",
		"RSHIFTASSIGN",
		"ANDASSIGN",
		"XORASSIGN",
		"ORASSIGN",
		"\"forall\"",
		"\"in\"",
		"RANGE",
		"\"sizeof\"",
		"\"lengthof\"",
		"\"is\"",
		"STRING_LITERAL"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap = new Hashtable();
		tokenTypeToASTClassMap.put(new Integer(4), datascript.ast.Field.class);
		tokenTypeToASTClassMap.put(new Integer(5), datascript.ast.StructType.class);
		tokenTypeToASTClassMap.put(new Integer(6), datascript.ast.UnionType.class);
		tokenTypeToASTClassMap.put(new Integer(8), datascript.ast.TypeInstantiation.class);
		tokenTypeToASTClassMap.put(new Integer(9), datascript.ast.ArrayType.class);
		tokenTypeToASTClassMap.put(new Integer(14), datascript.ast.TypeReference.class);
		tokenTypeToASTClassMap.put(new Integer(15), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(16), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(17), datascript.ast.EnumItem.class);
		tokenTypeToASTClassMap.put(new Integer(18), datascript.ast.BitFieldType.class);
		tokenTypeToASTClassMap.put(new Integer(19), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(20), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(21), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(22), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(23), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(24), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(25), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(26), datascript.ast.StdIntegerType.class);
		tokenTypeToASTClassMap.put(new Integer(27), datascript.ast.Expression.class);
		tokenTypeToASTClassMap.put(new Integer(28), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(29), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(30), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(31), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(32), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(33), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(34), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(35), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(36), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(37), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(38), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(39), datascript.ast.IntegerExpression.class);
		tokenTypeToASTClassMap.put(new Integer(40), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(41), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(42), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(43), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(44), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(45), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(46), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(47), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(48), datascript.ast.BooleanExpression.class);
		tokenTypeToASTClassMap.put(new Integer(49), datascript.ast.Expression.class);
		tokenTypeToASTClassMap.put(new Integer(50), datascript.ast.Expression.class);
		tokenTypeToASTClassMap.put(new Integer(51), datascript.ast.Expression.class);
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 7604609997426655232L, 23890821115L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 8757531502033502210L, 23890821115L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 74591420463185920L, 23890755584L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { -8975674057617833984L, 32748994544L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 2956613155368730624L, 65529L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { -162969061834096640L, 23890886655L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 4503599627370496L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { -8984681256872574976L, 32748994544L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { -8993688456127315968L, 32748994544L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 319755573543305216L, 1073872896L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 576460752303423488L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 432345564227567616L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { -8847321467969339392L, 65540L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { -9137803643934736384L, 65540L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { -8270860715665915904L, 65540L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 1297036692682702848L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 1310547491564814336L, 4L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 650770146155036672L, 49160L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 9045761878185213952L, 23890821115L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 72057594037927936L, 65520L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 82195091246022656L, 16368L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 72057594037927936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 1152921504606846976L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 12389297021779968L, 16368L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { -9136673345981382656L, 81908L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { -9209861237972664320L, 4L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 13510798882111488L, 4L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 13510798882111488L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 650770146155036672L, 16376L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { -7530300602232864768L, 9932046340L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 1617636072680849408L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { 0L, 131072L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { 74591420463185920L, 23622320128L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { -9147654716484747264L, 32748929024L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 540431955016024064L, 33554694128L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = { 463870761619161088L, 1073872896L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = { -7605735895991320576L, 1342046212L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = { 1616792266226008064L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = { 1617355216179429376L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = { 1617425584923607040L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = { 1617566322411962368L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = { 1617566391131439104L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = { 1617566528570392576L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = { 1617566803448299520L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = { 1617570101983182848L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = { 1617636124220456960L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = { 1617636125831069696L, 1073872900L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = { -7530018577231904768L, 33822801924L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = { 3377699720527872L, 8590000128L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	private static final long[] mk_tokenSet_50() {
		long[] data = { -7602358196270792704L, 9932046340L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	
	}
