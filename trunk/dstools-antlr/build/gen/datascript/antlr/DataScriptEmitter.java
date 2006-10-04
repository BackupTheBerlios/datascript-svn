// $ANTLR 2.7.6 (2005-12-22): "DataScriptEmitter.g" -> "DataScriptEmitter.java"$

package datascript.antlr;
import datascript.emit.Emitter;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class DataScriptEmitter extends antlr.TreeParser       implements DataScriptEmitterTokenTypes
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
public DataScriptEmitter() {
	tokenNames = _tokenNames;
}

	public final void translationUnit(AST _t) throws RecognitionException {
		
		AST translationUnit_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			em.beginTranslationUnit();
			AST __t2 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,MEMBERS);
			_t = _t.getFirstChild();
			{
			_loop4:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FIELD||_t.getType()==LITERAL_const)) {
					declaration(_t);
					_t = _retTree;
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			_t = __t2;
			_t = _t.getNextSibling();
			em.endTranslationUnit();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void declaration(AST _t) throws RecognitionException {
		
		AST declaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FIELD:
			{
				fieldDefinition(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_const:
			{
				constDeclaration(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldDefinition(AST _t) throws RecognitionException {
		
		AST fieldDefinition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST f = null;
		AST i = null;
		
		try {      // for error handling
			AST __t30 = _t;
			f = _t==ASTNULL ? null :(AST)_t;
			match(_t,FIELD);
			_t = _t.getFirstChild();
			typeReference(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ID:
			{
				i = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				em.beginField(f);
				break;
			}
			case 3:
			case LABEL:
			case COLON:
			case DOC:
			case ASSIGN:
			case LITERAL_if:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASSIGN:
			{
				fieldInitializer(_t);
				_t = _retTree;
				break;
			}
			case 3:
			case LABEL:
			case COLON:
			case DOC:
			case LITERAL_if:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_if:
			{
				fieldOptionalClause(_t);
				_t = _retTree;
				break;
			}
			case 3:
			case LABEL:
			case COLON:
			case DOC:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COLON:
			{
				fieldCondition(_t);
				_t = _retTree;
				break;
			}
			case 3:
			case LABEL:
			case DOC:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DOC:
			{
				AST tmp2_AST_in = (AST)_t;
				match(_t,DOC);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case LABEL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LABEL:
			{
				label(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t30;
			_t = _t.getNextSibling();
			if (i != null) em.endField(f);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void constDeclaration(AST _t) throws RecognitionException {
		
		AST constDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t28 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,LITERAL_const);
			_t = _t.getFirstChild();
			typeReference(_t);
			_t = _retTree;
			AST tmp4_AST_in = (AST)_t;
			match(_t,ID);
			_t = _t.getNextSibling();
			expression(_t);
			_t = _retTree;
			_t = __t28;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void label(AST _t) throws RecognitionException {
		
		AST label_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t7 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,LABEL);
			_t = _t.getFirstChild();
			expression(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INST:
			case CAST:
			case UPLUS:
			case UMINUS:
			case ARRAYELEM:
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case MULTIPLY:
			case DIVIDE:
			case MODULO:
			case LSHIFT:
			case RSHIFT:
			case OR:
			case XOR:
			case AND:
			case TILDE:
			case EQ:
			case NE:
			case LT:
			case LE:
			case GE:
			case GT:
			case LOGICALOR:
			case LOGICALAND:
			case BANG:
			case QUESTIONMARK:
			case DOT:
			case LPAREN:
			case ID:
			case COMMA:
			case ASSIGN:
			case MULTASSIGN:
			case DIVASSIGN:
			case MODASSIGN:
			case PLUSASSIGN:
			case MINUSASSIGN:
			case LSHIFTASSIGN:
			case RSHIFTASSIGN:
			case ANDASSIGN:
			case XORASSIGN:
			case ORASSIGN:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case LITERAL_is:
			case STRING_LITERAL:
			{
				expression(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t7;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void expression(AST _t) throws RecognitionException {
		
		AST expression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case COMMA:
			{
				AST __t82 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,COMMA);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t82;
				_t = _t.getNextSibling();
				break;
			}
			case ASSIGN:
			{
				AST __t83 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,ASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t83;
				_t = _t.getNextSibling();
				break;
			}
			case MULTASSIGN:
			{
				AST __t84 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,MULTASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t84;
				_t = _t.getNextSibling();
				break;
			}
			case DIVASSIGN:
			{
				AST __t85 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,DIVASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t85;
				_t = _t.getNextSibling();
				break;
			}
			case MODASSIGN:
			{
				AST __t86 = _t;
				AST tmp10_AST_in = (AST)_t;
				match(_t,MODASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t86;
				_t = _t.getNextSibling();
				break;
			}
			case PLUSASSIGN:
			{
				AST __t87 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,PLUSASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t87;
				_t = _t.getNextSibling();
				break;
			}
			case MINUSASSIGN:
			{
				AST __t88 = _t;
				AST tmp12_AST_in = (AST)_t;
				match(_t,MINUSASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t88;
				_t = _t.getNextSibling();
				break;
			}
			case LSHIFTASSIGN:
			{
				AST __t89 = _t;
				AST tmp13_AST_in = (AST)_t;
				match(_t,LSHIFTASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t89;
				_t = _t.getNextSibling();
				break;
			}
			case RSHIFTASSIGN:
			{
				AST __t90 = _t;
				AST tmp14_AST_in = (AST)_t;
				match(_t,RSHIFTASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t90;
				_t = _t.getNextSibling();
				break;
			}
			case ANDASSIGN:
			{
				AST __t91 = _t;
				AST tmp15_AST_in = (AST)_t;
				match(_t,ANDASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t91;
				_t = _t.getNextSibling();
				break;
			}
			case XORASSIGN:
			{
				AST __t92 = _t;
				AST tmp16_AST_in = (AST)_t;
				match(_t,XORASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t92;
				_t = _t.getNextSibling();
				break;
			}
			case ORASSIGN:
			{
				AST __t93 = _t;
				AST tmp17_AST_in = (AST)_t;
				match(_t,ORASSIGN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t93;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_forall:
			{
				AST __t94 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,LITERAL_forall);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t94;
				_t = _t.getNextSibling();
				break;
			}
			case QUESTIONMARK:
			{
				AST __t95 = _t;
				AST tmp19_AST_in = (AST)_t;
				match(_t,QUESTIONMARK);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t95;
				_t = _t.getNextSibling();
				break;
			}
			case LOGICALOR:
			{
				AST __t96 = _t;
				AST tmp20_AST_in = (AST)_t;
				match(_t,LOGICALOR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t96;
				_t = _t.getNextSibling();
				break;
			}
			case LOGICALAND:
			{
				AST __t97 = _t;
				AST tmp21_AST_in = (AST)_t;
				match(_t,LOGICALAND);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t97;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			{
				AST __t98 = _t;
				AST tmp22_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t98;
				_t = _t.getNextSibling();
				break;
			}
			case XOR:
			{
				AST __t99 = _t;
				AST tmp23_AST_in = (AST)_t;
				match(_t,XOR);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t99;
				_t = _t.getNextSibling();
				break;
			}
			case AND:
			{
				AST __t100 = _t;
				AST tmp24_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t100;
				_t = _t.getNextSibling();
				break;
			}
			case EQ:
			{
				AST __t101 = _t;
				AST tmp25_AST_in = (AST)_t;
				match(_t,EQ);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t101;
				_t = _t.getNextSibling();
				break;
			}
			case NE:
			{
				AST __t102 = _t;
				AST tmp26_AST_in = (AST)_t;
				match(_t,NE);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t102;
				_t = _t.getNextSibling();
				break;
			}
			case LT:
			{
				AST __t103 = _t;
				AST tmp27_AST_in = (AST)_t;
				match(_t,LT);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t103;
				_t = _t.getNextSibling();
				break;
			}
			case GT:
			{
				AST __t104 = _t;
				AST tmp28_AST_in = (AST)_t;
				match(_t,GT);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t104;
				_t = _t.getNextSibling();
				break;
			}
			case LE:
			{
				AST __t105 = _t;
				AST tmp29_AST_in = (AST)_t;
				match(_t,LE);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t105;
				_t = _t.getNextSibling();
				break;
			}
			case GE:
			{
				AST __t106 = _t;
				AST tmp30_AST_in = (AST)_t;
				match(_t,GE);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t106;
				_t = _t.getNextSibling();
				break;
			}
			case LSHIFT:
			{
				AST __t107 = _t;
				AST tmp31_AST_in = (AST)_t;
				match(_t,LSHIFT);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t107;
				_t = _t.getNextSibling();
				break;
			}
			case RSHIFT:
			{
				AST __t108 = _t;
				AST tmp32_AST_in = (AST)_t;
				match(_t,RSHIFT);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t108;
				_t = _t.getNextSibling();
				break;
			}
			case PLUS:
			{
				AST __t109 = _t;
				AST tmp33_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t109;
				_t = _t.getNextSibling();
				break;
			}
			case MINUS:
			{
				AST __t110 = _t;
				AST tmp34_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t110;
				_t = _t.getNextSibling();
				break;
			}
			case MULTIPLY:
			{
				AST __t111 = _t;
				AST tmp35_AST_in = (AST)_t;
				match(_t,MULTIPLY);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t111;
				_t = _t.getNextSibling();
				break;
			}
			case DIVIDE:
			{
				AST __t112 = _t;
				AST tmp36_AST_in = (AST)_t;
				match(_t,DIVIDE);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t112;
				_t = _t.getNextSibling();
				break;
			}
			case MODULO:
			{
				AST __t113 = _t;
				AST tmp37_AST_in = (AST)_t;
				match(_t,MODULO);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t113;
				_t = _t.getNextSibling();
				break;
			}
			case CAST:
			{
				AST __t114 = _t;
				AST tmp38_AST_in = (AST)_t;
				match(_t,CAST);
				_t = _t.getFirstChild();
				definedType(_t);
				_t = _retTree;
				expression(_t);
				_t = _retTree;
				_t = __t114;
				_t = _t.getNextSibling();
				break;
			}
			case UPLUS:
			{
				AST __t115 = _t;
				AST tmp39_AST_in = (AST)_t;
				match(_t,UPLUS);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t115;
				_t = _t.getNextSibling();
				break;
			}
			case UMINUS:
			{
				AST __t116 = _t;
				AST tmp40_AST_in = (AST)_t;
				match(_t,UMINUS);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t116;
				_t = _t.getNextSibling();
				break;
			}
			case TILDE:
			{
				AST __t117 = _t;
				AST tmp41_AST_in = (AST)_t;
				match(_t,TILDE);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t117;
				_t = _t.getNextSibling();
				break;
			}
			case BANG:
			{
				AST __t118 = _t;
				AST tmp42_AST_in = (AST)_t;
				match(_t,BANG);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t118;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_sizeof:
			{
				AST __t119 = _t;
				AST tmp43_AST_in = (AST)_t;
				match(_t,LITERAL_sizeof);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t119;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_lengthof:
			{
				AST __t120 = _t;
				AST tmp44_AST_in = (AST)_t;
				match(_t,LITERAL_lengthof);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t120;
				_t = _t.getNextSibling();
				break;
			}
			case DOT:
			{
				AST __t121 = _t;
				AST tmp45_AST_in = (AST)_t;
				match(_t,DOT);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t121;
				_t = _t.getNextSibling();
				break;
			}
			case ARRAYELEM:
			{
				AST __t122 = _t;
				AST tmp46_AST_in = (AST)_t;
				match(_t,ARRAYELEM);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t122;
				_t = _t.getNextSibling();
				break;
			}
			case INST:
			{
				AST __t123 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,INST);
				_t = _t.getFirstChild();
				{
				int _cnt125=0;
				_loop125:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_0.member(_t.getType()))) {
						expression(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt125>=1 ) { break _loop125; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt125++;
				} while (true);
				}
				_t = __t123;
				_t = _t.getNextSibling();
				break;
			}
			case LPAREN:
			{
				AST __t126 = _t;
				AST tmp48_AST_in = (AST)_t;
				match(_t,LPAREN);
				_t = _t.getFirstChild();
				expression(_t);
				_t = _retTree;
				_t = __t126;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_is:
			{
				AST __t127 = _t;
				AST tmp49_AST_in = (AST)_t;
				match(_t,LITERAL_is);
				_t = _t.getFirstChild();
				AST tmp50_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				_t = __t127;
				_t = _t.getNextSibling();
				break;
			}
			case ID:
			{
				AST tmp51_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			case INTEGER_LITERAL:
			{
				AST tmp52_AST_in = (AST)_t;
				match(_t,INTEGER_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case STRING_LITERAL:
			{
				AST tmp53_AST_in = (AST)_t;
				match(_t,STRING_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void parameterList(AST _t) throws RecognitionException {
		
		AST parameterList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t10 = _t;
			AST tmp54_AST_in = (AST)_t;
			match(_t,PARAM);
			_t = _t.getFirstChild();
			{
			int _cnt12=0;
			_loop12:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_1.member(_t.getType()))) {
					definedType(_t);
					_t = _retTree;
					AST tmp55_AST_in = (AST)_t;
					match(_t,ID);
					_t = _t.getNextSibling();
				}
				else {
					if ( _cnt12>=1 ) { break _loop12; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt12++;
			} while (true);
			}
			_t = __t10;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void definedType(AST _t) throws RecognitionException {
		
		AST definedType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPEREF:
			{
				AST __t65 = _t;
				AST tmp56_AST_in = (AST)_t;
				match(_t,TYPEREF);
				_t = _t.getFirstChild();
				AST tmp57_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				{
				_loop67:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==DOT)) {
						AST tmp58_AST_in = (AST)_t;
						match(_t,DOT);
						_t = _t.getNextSibling();
						AST tmp59_AST_in = (AST)_t;
						match(_t,ID);
						_t = _t.getNextSibling();
					}
					else {
						break _loop67;
					}
					
				} while (true);
				}
				_t = __t65;
				_t = _t.getNextSibling();
				break;
			}
			case BIT:
			case UINT8:
			case UINT16:
			case UINT32:
			case UINT64:
			case INT8:
			case INT16:
			case INT32:
			case INT64:
			case LITERAL_string:
			case LITERAL_big:
			case LITERAL_little:
			{
				builtinType(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
/******************* begin of enumerator stuff *****************/
	public final void enumDeclaration(AST _t) throws RecognitionException {
		
		AST enumDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST e = null;
		
		try {      // for error handling
			AST __t14 = _t;
			e = _t==ASTNULL ? null :(AST)_t;
			match(_t,LITERAL_enum);
			_t = _t.getFirstChild();
			em.beginEnumeration(e);
			builtinType(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ID:
			{
				AST tmp60_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			enumMemberList(_t);
			_t = _retTree;
			_t = __t14;
			_t = _t.getNextSibling();
			em.endEnumeration(e);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void builtinType(AST _t) throws RecognitionException {
		
		AST builtinType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_big:
			case LITERAL_little:
			{
				byteOrderModifier(_t);
				_t = _retTree;
				break;
			}
			case BIT:
			case UINT8:
			case UINT16:
			case UINT32:
			case UINT64:
			case INT8:
			case INT16:
			case INT32:
			case INT64:
			case LITERAL_string:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			builtinTypeDefaultOrder(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void enumMemberList(AST _t) throws RecognitionException {
		
		AST enumMemberList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t17 = _t;
			AST tmp61_AST_in = (AST)_t;
			match(_t,MEMBERS);
			_t = _t.getFirstChild();
			{
			int _cnt19=0;
			_loop19:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ITEM)) {
					enumItem(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt19>=1 ) { break _loop19; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt19++;
			} while (true);
			}
			_t = __t17;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void enumItem(AST _t) throws RecognitionException {
		
		AST enumItem_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		
		try {      // for error handling
			AST __t21 = _t;
			i = _t==ASTNULL ? null :(AST)_t;
			match(_t,ITEM);
			_t = _t.getFirstChild();
			em.beginEnumItem(i);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DOC:
			{
				AST tmp62_AST_in = (AST)_t;
				match(_t,DOC);
				_t = _t.getNextSibling();
				break;
			}
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST tmp63_AST_in = (AST)_t;
			match(_t,ID);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INST:
			case CAST:
			case UPLUS:
			case UMINUS:
			case ARRAYELEM:
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case MULTIPLY:
			case DIVIDE:
			case MODULO:
			case LSHIFT:
			case RSHIFT:
			case OR:
			case XOR:
			case AND:
			case TILDE:
			case EQ:
			case NE:
			case LT:
			case LE:
			case GE:
			case GT:
			case LOGICALOR:
			case LOGICALAND:
			case BANG:
			case QUESTIONMARK:
			case DOT:
			case LPAREN:
			case ID:
			case COMMA:
			case ASSIGN:
			case MULTASSIGN:
			case DIVASSIGN:
			case MODASSIGN:
			case PLUSASSIGN:
			case MINUSASSIGN:
			case LSHIFTASSIGN:
			case RSHIFTASSIGN:
			case ANDASSIGN:
			case XORASSIGN:
			case ORASSIGN:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case LITERAL_is:
			case STRING_LITERAL:
			{
				expression(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t21;
			_t = _t.getNextSibling();
			em.endEnumItem(i);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void bitmaskDeclaration(AST _t) throws RecognitionException {
		
		AST bitmaskDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t25 = _t;
			AST tmp64_AST_in = (AST)_t;
			match(_t,LITERAL_bitmask);
			_t = _t.getFirstChild();
			builtinType(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ID:
			{
				AST tmp65_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			enumMemberList(_t);
			_t = _retTree;
			_t = __t25;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeReference(AST _t) throws RecognitionException {
		
		AST typeReference_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRUCT:
			{
				structDeclaration(_t);
				_t = _retTree;
				break;
			}
			case UNION:
			{
				unionDeclaration(_t);
				_t = _retTree;
				break;
			}
			case TYPEREF:
			case BIT:
			case UINT8:
			case UINT16:
			case UINT32:
			case UINT64:
			case INT8:
			case INT16:
			case INT32:
			case INT64:
			case LITERAL_string:
			case LITERAL_big:
			case LITERAL_little:
			{
				definedType(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_enum:
			{
				enumDeclaration(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_bitmask:
			{
				bitmaskDeclaration(_t);
				_t = _retTree;
				break;
			}
			case ARRAY:
			{
				arrayType(_t);
				_t = _retTree;
				break;
			}
			case INST:
			{
				paramTypeInstantiation(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldInitializer(AST _t) throws RecognitionException {
		
		AST fieldInitializer_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t41 = _t;
			AST tmp66_AST_in = (AST)_t;
			match(_t,ASSIGN);
			_t = _t.getFirstChild();
			typeValue(_t);
			_t = _retTree;
			_t = __t41;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldOptionalClause(AST _t) throws RecognitionException {
		
		AST fieldOptionalClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t43 = _t;
			AST tmp67_AST_in = (AST)_t;
			match(_t,LITERAL_if);
			_t = _t.getFirstChild();
			expression(_t);
			_t = _retTree;
			_t = __t43;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldCondition(AST _t) throws RecognitionException {
		
		AST fieldCondition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t45 = _t;
			AST tmp68_AST_in = (AST)_t;
			match(_t,COLON);
			_t = _t.getFirstChild();
			expression(_t);
			_t = _retTree;
			_t = __t45;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeArgumentList(AST _t) throws RecognitionException {
		
		AST typeArgumentList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			int _cnt39=0;
			_loop39:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					expression(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt39>=1 ) { break _loop39; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt39++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeValue(AST _t) throws RecognitionException {
		
		AST typeValue_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INST:
			case CAST:
			case UPLUS:
			case UMINUS:
			case ARRAYELEM:
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case MULTIPLY:
			case DIVIDE:
			case MODULO:
			case LSHIFT:
			case RSHIFT:
			case OR:
			case XOR:
			case AND:
			case TILDE:
			case EQ:
			case NE:
			case LT:
			case LE:
			case GE:
			case GT:
			case LOGICALOR:
			case LOGICALAND:
			case BANG:
			case QUESTIONMARK:
			case DOT:
			case LPAREN:
			case ID:
			case COMMA:
			case ASSIGN:
			case MULTASSIGN:
			case DIVASSIGN:
			case MODASSIGN:
			case PLUSASSIGN:
			case MINUSASSIGN:
			case LSHIFTASSIGN:
			case RSHIFTASSIGN:
			case ANDASSIGN:
			case XORASSIGN:
			case ORASSIGN:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case LITERAL_is:
			case STRING_LITERAL:
			{
				expression(_t);
				_t = _retTree;
				break;
			}
			case LCURLY:
			{
				AST __t78 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,LCURLY);
				_t = _t.getFirstChild();
				{
				int _cnt80=0;
				_loop80:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_2.member(_t.getType()))) {
						typeValue(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt80>=1 ) { break _loop80; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt80++;
				} while (true);
				}
				_t = __t78;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeDeclaration(AST _t) throws RecognitionException {
		
		AST typeDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRUCT:
			{
				structDeclaration(_t);
				_t = _retTree;
				break;
			}
			case UNION:
			{
				unionDeclaration(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_enum:
			{
				enumDeclaration(_t);
				_t = _retTree;
				break;
			}
			case LITERAL_bitmask:
			{
				bitmaskDeclaration(_t);
				_t = _retTree;
				break;
			}
			case ARRAY:
			{
				arrayType(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void structDeclaration(AST _t) throws RecognitionException {
		
		AST structDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST s = null;
		
		try {      // for error handling
			AST __t53 = _t;
			s = _t==ASTNULL ? null :(AST)_t;
			match(_t,STRUCT);
			_t = _t.getFirstChild();
			em.beginSequence(s);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ID:
			{
				AST tmp70_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			case PARAM:
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PARAM:
			{
				parameterList(_t);
				_t = _retTree;
				break;
			}
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			memberList(_t);
			_t = _retTree;
			_t = __t53;
			_t = _t.getNextSibling();
			em.endSequence(s);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void unionDeclaration(AST _t) throws RecognitionException {
		
		AST unionDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST u = null;
		
		try {      // for error handling
			AST __t57 = _t;
			u = _t==ASTNULL ? null :(AST)_t;
			match(_t,UNION);
			_t = _t.getFirstChild();
			em.beginUnion(u);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ID:
			{
				AST tmp71_AST_in = (AST)_t;
				match(_t,ID);
				_t = _t.getNextSibling();
				break;
			}
			case PARAM:
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PARAM:
			{
				parameterList(_t);
				_t = _retTree;
				break;
			}
			case MEMBERS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			memberList(_t);
			_t = _retTree;
			_t = __t57;
			_t = _t.getNextSibling();
			em.endUnion(u);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void arrayType(AST _t) throws RecognitionException {
		
		AST arrayType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t49 = _t;
			AST tmp72_AST_in = (AST)_t;
			match(_t,ARRAY);
			_t = _t.getFirstChild();
			typeReference(_t);
			_t = _retTree;
			arrayRange(_t);
			_t = _retTree;
			_t = __t49;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void paramTypeInstantiation(AST _t) throws RecognitionException {
		
		AST paramTypeInstantiation_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t51 = _t;
			AST tmp73_AST_in = (AST)_t;
			match(_t,INST);
			_t = _t.getFirstChild();
			definedType(_t);
			_t = _retTree;
			typeArgumentList(_t);
			_t = _retTree;
			_t = __t51;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void arrayRange(AST _t) throws RecognitionException {
		
		AST arrayRange_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INST:
			case CAST:
			case UPLUS:
			case UMINUS:
			case ARRAYELEM:
			case INTEGER_LITERAL:
			case PLUS:
			case MINUS:
			case MULTIPLY:
			case DIVIDE:
			case MODULO:
			case LSHIFT:
			case RSHIFT:
			case OR:
			case XOR:
			case AND:
			case TILDE:
			case EQ:
			case NE:
			case LT:
			case LE:
			case GE:
			case GT:
			case LOGICALOR:
			case LOGICALAND:
			case BANG:
			case QUESTIONMARK:
			case DOT:
			case LPAREN:
			case ID:
			case COMMA:
			case ASSIGN:
			case MULTASSIGN:
			case DIVASSIGN:
			case MODASSIGN:
			case PLUSASSIGN:
			case MINUSASSIGN:
			case LSHIFTASSIGN:
			case RSHIFTASSIGN:
			case ANDASSIGN:
			case XORASSIGN:
			case ORASSIGN:
			case LITERAL_forall:
			case LITERAL_sizeof:
			case LITERAL_lengthof:
			case LITERAL_is:
			case STRING_LITERAL:
			{
				expression(_t);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case INST:
				case CAST:
				case UPLUS:
				case UMINUS:
				case ARRAYELEM:
				case INTEGER_LITERAL:
				case PLUS:
				case MINUS:
				case MULTIPLY:
				case DIVIDE:
				case MODULO:
				case LSHIFT:
				case RSHIFT:
				case OR:
				case XOR:
				case AND:
				case TILDE:
				case EQ:
				case NE:
				case LT:
				case LE:
				case GE:
				case GT:
				case LOGICALOR:
				case LOGICALAND:
				case BANG:
				case QUESTIONMARK:
				case DOT:
				case LPAREN:
				case ID:
				case COMMA:
				case ASSIGN:
				case MULTASSIGN:
				case DIVASSIGN:
				case MODASSIGN:
				case PLUSASSIGN:
				case MINUSASSIGN:
				case LSHIFTASSIGN:
				case RSHIFTASSIGN:
				case ANDASSIGN:
				case XORASSIGN:
				case ORASSIGN:
				case LITERAL_forall:
				case LITERAL_sizeof:
				case LITERAL_lengthof:
				case LITERAL_is:
				case STRING_LITERAL:
				{
					expression(_t);
					_t = _retTree;
					break;
				}
				case 3:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void memberList(AST _t) throws RecognitionException {
		
		AST memberList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t61 = _t;
			AST tmp74_AST_in = (AST)_t;
			match(_t,MEMBERS);
			_t = _t.getFirstChild();
			{
			_loop63:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FIELD||_t.getType()==LITERAL_const)) {
					declaration(_t);
					_t = _retTree;
				}
				else {
					break _loop63;
				}
				
			} while (true);
			}
			_t = __t61;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void byteOrderModifier(AST _t) throws RecognitionException {
		
		AST byteOrderModifier_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_big:
			{
				AST tmp75_AST_in = (AST)_t;
				match(_t,LITERAL_big);
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_little:
			{
				AST tmp76_AST_in = (AST)_t;
				match(_t,LITERAL_little);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void builtinTypeDefaultOrder(AST _t) throws RecognitionException {
		
		AST builtinTypeDefaultOrder_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UINT8:
			{
				AST tmp77_AST_in = (AST)_t;
				match(_t,UINT8);
				_t = _t.getNextSibling();
				break;
			}
			case UINT16:
			{
				AST tmp78_AST_in = (AST)_t;
				match(_t,UINT16);
				_t = _t.getNextSibling();
				break;
			}
			case UINT32:
			{
				AST tmp79_AST_in = (AST)_t;
				match(_t,UINT32);
				_t = _t.getNextSibling();
				break;
			}
			case UINT64:
			{
				AST tmp80_AST_in = (AST)_t;
				match(_t,UINT64);
				_t = _t.getNextSibling();
				break;
			}
			case INT8:
			{
				AST tmp81_AST_in = (AST)_t;
				match(_t,INT8);
				_t = _t.getNextSibling();
				break;
			}
			case INT16:
			{
				AST tmp82_AST_in = (AST)_t;
				match(_t,INT16);
				_t = _t.getNextSibling();
				break;
			}
			case INT32:
			{
				AST tmp83_AST_in = (AST)_t;
				match(_t,INT32);
				_t = _t.getNextSibling();
				break;
			}
			case INT64:
			{
				AST tmp84_AST_in = (AST)_t;
				match(_t,INT64);
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_string:
			{
				AST tmp85_AST_in = (AST)_t;
				match(_t,LITERAL_string);
				_t = _t.getNextSibling();
				break;
			}
			case BIT:
			{
				bitField(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void bitField(AST _t) throws RecognitionException {
		
		AST bitField_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t72 = _t;
			AST tmp86_AST_in = (AST)_t;
			match(_t,BIT);
			_t = _t.getFirstChild();
			expression(_t);
			_t = _retTree;
			_t = __t72;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
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
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { -9002695655247736576L, 32748863488L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 133971968L, 53248L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { -8426234902944313088L, 32748863488L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	}
	
