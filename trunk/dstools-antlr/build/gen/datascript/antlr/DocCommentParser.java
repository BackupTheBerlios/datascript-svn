// $ANTLR 2.7.6 (2005-12-22): "DocComment.g" -> "DocCommentParser.java"$

package datascript.antlr;

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

public class DocCommentParser extends antlr.LLkParser       implements DocCommentParserTokenTypes
 {

protected DocCommentParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DocCommentParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected DocCommentParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public DocCommentParser(TokenStream lexer) {
  this(lexer,3);
}

public DocCommentParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void comment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comment_AST = null;
		
		try {      // for error handling
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp1_AST);
			match(5);
			{
			_loop3:
			do {
				if ((LA(1)==NEWLINE)) {
					match(NEWLINE);
				}
				else {
					break _loop3;
				}
				
			} while (true);
			}
			commentBody();
			astFactory.addASTChild(currentAST, returnAST);
			filler();
			astFactory.addASTChild(currentAST, returnAST);
			match(EOC);
			match(Token.EOF_TYPE);
			comment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = comment_AST;
	}
	
	public final void commentBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commentBody_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case TEXT:
			{
				commentText();
				astFactory.addASTChild(currentAST, returnAST);
				{
				boolean synPredMatched12 = false;
				if (((LA(1)==NEWLINE) && (LA(2)==NEWLINE||LA(2)==AT) && (LA(3)==NEWLINE||LA(3)==AT||LA(3)==TEXT))) {
					int _m12 = mark();
					synPredMatched12 = true;
					inputState.guessing++;
					try {
						{
						{
						int _cnt11=0;
						_loop11:
						do {
							if ((LA(1)==NEWLINE)) {
								match(NEWLINE);
							}
							else {
								if ( _cnt11>=1 ) { break _loop11; } else {throw new NoViableAltException(LT(1), getFilename());}
							}
							
							_cnt11++;
						} while (true);
						}
						match(AT);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched12 = false;
					}
					rewind(_m12);
inputState.guessing--;
				}
				if ( synPredMatched12 ) {
					{
					int _cnt14=0;
					_loop14:
					do {
						if ((LA(1)==NEWLINE)) {
							match(NEWLINE);
						}
						else {
							if ( _cnt14>=1 ) { break _loop14; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt14++;
					} while (true);
					}
					tags();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==EOF||LA(1)==NEWLINE||LA(1)==EOC) && (LA(2)==EOF||LA(2)==NEWLINE||LA(2)==EOC) && (LA(3)==EOF||LA(3)==NEWLINE||LA(3)==EOC)) {
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				
				}
				commentBody_AST = (AST)currentAST.root;
				break;
			}
			case AT:
			{
				tags();
				astFactory.addASTChild(currentAST, returnAST);
				commentBody_AST = (AST)currentAST.root;
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
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = commentBody_AST;
	}
	
	public final void filler() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filler_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NEWLINE:
			{
				AST tmp6_AST = null;
				tmp6_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp6_AST);
				match(NEWLINE);
				filler();
				astFactory.addASTChild(currentAST, returnAST);
				filler_AST = (AST)currentAST.root;
				break;
			}
			case EOC:
			{
				filler_AST = (AST)currentAST.root;
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
		returnAST = filler_AST;
	}
	
	public final void strippedComment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST strippedComment_AST = null;
		
		try {      // for error handling
			{
			_loop6:
			do {
				if ((LA(1)==NEWLINE)) {
					AST tmp7_AST = null;
					tmp7_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp7_AST);
					match(NEWLINE);
				}
				else {
					break _loop6;
				}
				
			} while (true);
			}
			commentBody();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp8_AST);
			match(Token.EOF_TYPE);
			strippedComment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = strippedComment_AST;
	}
	
	public final void commentText() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commentText_AST = null;
		
		try {      // for error handling
			commentParagraph();
			astFactory.addASTChild(currentAST, returnAST);
			{
			boolean synPredMatched20 = false;
			if (((LA(1)==NEWLINE) && (LA(2)==NEWLINE) && (LA(3)==NEWLINE||LA(3)==TEXT))) {
				int _m20 = mark();
				synPredMatched20 = true;
				inputState.guessing++;
				try {
					{
					{
					int _cnt19=0;
					_loop19:
					do {
						if ((LA(1)==NEWLINE)) {
							match(NEWLINE);
						}
						else {
							if ( _cnt19>=1 ) { break _loop19; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt19++;
					} while (true);
					}
					match(TEXT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched20 = false;
				}
				rewind(_m20);
inputState.guessing--;
			}
			if ( synPredMatched20 ) {
				match(NEWLINE);
				{
				int _cnt22=0;
				_loop22:
				do {
					if ((LA(1)==NEWLINE)) {
						match(NEWLINE);
					}
					else {
						if ( _cnt22>=1 ) { break _loop22; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt22++;
				} while (true);
				}
				commentText();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==EOF||LA(1)==NEWLINE||LA(1)==EOC) && (_tokenSet_3.member(LA(2))) && (_tokenSet_4.member(LA(3)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			commentText_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = commentText_AST;
	}
	
	public final void tags() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tags_AST = null;
		
		try {      // for error handling
			tag();
			astFactory.addASTChild(currentAST, returnAST);
			{
			boolean synPredMatched31 = false;
			if (((LA(1)==NEWLINE) && (LA(2)==NEWLINE||LA(2)==AT) && (LA(3)==NEWLINE||LA(3)==AT||LA(3)==TEXT))) {
				int _m31 = mark();
				synPredMatched31 = true;
				inputState.guessing++;
				try {
					{
					{
					int _cnt30=0;
					_loop30:
					do {
						if ((LA(1)==NEWLINE)) {
							match(NEWLINE);
						}
						else {
							if ( _cnt30>=1 ) { break _loop30; } else {throw new NoViableAltException(LT(1), getFilename());}
						}
						
						_cnt30++;
					} while (true);
					}
					match(AT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched31 = false;
				}
				rewind(_m31);
inputState.guessing--;
			}
			if ( synPredMatched31 ) {
				{
				int _cnt33=0;
				_loop33:
				do {
					if ((LA(1)==NEWLINE)) {
						match(NEWLINE);
					}
					else {
						if ( _cnt33>=1 ) { break _loop33; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt33++;
				} while (true);
				}
				tags();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==EOF||LA(1)==NEWLINE||LA(1)==EOC) && (LA(2)==EOF||LA(2)==NEWLINE||LA(2)==EOC) && (LA(3)==EOF||LA(3)==NEWLINE||LA(3)==EOC)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			tags_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = tags_AST;
	}
	
	public final void commentParagraph() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST commentParagraph_AST = null;
		
		try {      // for error handling
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp12_AST);
			match(TEXT);
			{
			_loop25:
			do {
				if ((LA(1)==NEWLINE) && (LA(2)==TEXT)) {
					AST tmp13_AST = null;
					tmp13_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp13_AST);
					match(NEWLINE);
					AST tmp14_AST = null;
					tmp14_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp14_AST);
					match(TEXT);
				}
				else {
					break _loop25;
				}
				
			} while (true);
			}
			commentParagraph_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = commentParagraph_AST;
	}
	
	public final void tag() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tag_AST = null;
		
		try {      // for error handling
			AST tmp15_AST = null;
			tmp15_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp15_AST);
			match(AT);
			AST tmp16_AST = null;
			tmp16_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp16_AST);
			match(TEXT);
			{
			_loop39:
			do {
				if ((LA(1)==NEWLINE) && (LA(2)==TEXT)) {
					AST tmp17_AST = null;
					tmp17_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp17_AST);
					match(NEWLINE);
					AST tmp18_AST = null;
					tmp18_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp18_AST);
					match(TEXT);
				}
				else {
					break _loop39;
				}
				
			} while (true);
			}
			tag_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_1);
			} else {
			  throw ex;
			}
		}
		returnAST = tag_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"FIELD",
		"\"/**\"",
		"NEWLINE",
		"EOC",
		"AT",
		"TEXT",
		"PREFIX",
		"WS",
		"STAR",
		"NL",
		"TAGNAME"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 194L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 450L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 962L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
