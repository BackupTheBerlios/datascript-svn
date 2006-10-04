// $ANTLR 2.7.6 (2005-12-22): "DocComment.g" -> "DocCommentLexer.java"$

package datascript.antlr;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

public class DocCommentLexer extends antlr.CharScanner implements DocCommentParserTokenTypes, TokenStream
 {
public DocCommentLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public DocCommentLexer(Reader in) {
	this(new CharBuffer(in));
}
public DocCommentLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public DocCommentLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("/**", this), new Integer(5));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '\n':  case '\r':
				{
					mNEWLINE(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mEOC(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((_tokenSet_0.member(LA(1)))) {
						mTEXT(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mNEWLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NEWLINE;
		int _saveIndex;
		
		mNL(false);
		{
		if ((_tokenSet_1.member(LA(1)))) {
			_saveIndex=text.length();
			mPREFIX(false);
			text.setLength(_saveIndex);
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NL;
		int _saveIndex;
		
		if ((LA(1)=='\r') && (LA(2)=='\n')) {
			_saveIndex=text.length();
			match('\r');
			text.setLength(_saveIndex);
			match('\n');
			newline();
		}
		else if ((LA(1)=='\r') && (true)) {
			match('\r');
			newline();
		}
		else if ((LA(1)=='\n')) {
			match('\n');
			newline();
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mPREFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PREFIX;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '\t':  case '\u000c':  case ' ':
		{
			_saveIndex=text.length();
			mWS(false);
			text.setLength(_saveIndex);
			{
			if (((LA(1)=='*'))&&(LA(2) != '/')) {
				{
				int _cnt45=0;
				_loop45:
				do {
					if ((LA(1)=='*')) {
						_saveIndex=text.length();
						mSTAR(false);
						text.setLength(_saveIndex);
					}
					else {
						if ( _cnt45>=1 ) { break _loop45; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt45++;
				} while (true);
				}
				{
				if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
					_saveIndex=text.length();
					mWS(false);
					text.setLength(_saveIndex);
				}
				else {
				}
				
				}
			}
			else {
			}
			
			}
			break;
		}
		case '*':
		{
			{
			int _cnt48=0;
			_loop48:
			do {
				if ((LA(1)=='*')) {
					_saveIndex=text.length();
					mSTAR(false);
					text.setLength(_saveIndex);
				}
				else {
					if ( _cnt48>=1 ) { break _loop48; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt48++;
			} while (true);
			}
			{
			if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
				_saveIndex=text.length();
				mWS(false);
				text.setLength(_saveIndex);
			}
			else {
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		int _cnt52=0;
		_loop52:
		do {
			switch ( LA(1)) {
			case ' ':
			{
				_saveIndex=text.length();
				match(' ');
				text.setLength(_saveIndex);
				break;
			}
			case '\t':
			{
				_saveIndex=text.length();
				match('\t');
				text.setLength(_saveIndex);
				break;
			}
			case '\u000c':
			{
				_saveIndex=text.length();
				match('\f');
				text.setLength(_saveIndex);
				break;
			}
			default:
			{
				if ( _cnt52>=1 ) { break _loop52; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt52++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		if (!(LA(2) != '/'))
		  throw new SemanticException("LA(2) != '/'");
		_saveIndex=text.length();
		match("*");
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEOC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EOC;
		int _saveIndex;
		
		match("*/");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TEXT;
		int _saveIndex;
		
		{
		if ((LA(1)=='/') && (LA(2)=='*') && (LA(3)=='*') && (true)) {
			match("/**");
			{
			if ((_tokenSet_1.member(LA(1)))) {
				mPREFIX(false);
			}
			else {
			}
			
			}
		}
		else if (((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2))) && (true) && (true))&&(!(LA(1)=='/' && LA(2) == '*'))) {
			{
			match(_tokenSet_2);
			}
			{
			int _cnt61=0;
			_loop61:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					{
					match(_tokenSet_4);
					}
				}
				else if (((LA(1)=='*'))&&(LA(2) != '/')) {
					match('*');
				}
				else {
					if ( _cnt61>=1 ) { break _loop61; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt61++;
			} while (true);
			}
		}
		else if ((LA(1)=='@')) {
			mAT(false);
			_ttype = AT;
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AT;
		int _saveIndex;
		
		_saveIndex=text.length();
		match("@");
		text.setLength(_saveIndex);
		mTAGNAME(false);
		mWS(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mTAGNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TAGNAME;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'p':
		{
			match("param");
			break;
		}
		case 's':
		{
			match("see");
			break;
		}
		case 't':
		{
			match("todo");
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[1024];
		data[0]=-4402341492232L;
		for (int i = 1; i<=510; i++) { data[i]=-1L; }
		data[511]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[513];
		data[0]=4402341483008L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[1024];
		data[0]=-4402341492232L;
		data[1]=-2L;
		for (int i = 2; i<=510; i++) { data[i]=-1L; }
		data[511]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[1024];
		data[0]=-9224L;
		for (int i = 1; i<=510; i++) { data[i]=-1L; }
		data[511]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[1024];
		data[0]=-4398046520328L;
		for (int i = 1; i<=510; i++) { data[i]=-1L; }
		data[511]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
