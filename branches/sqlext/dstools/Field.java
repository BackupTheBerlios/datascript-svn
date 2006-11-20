/*
 * Field.java
 *
 * @author: Godmar Back
 * @version: $Id: Field.java,v 1.2 2003/06/19 19:53:34 gback Exp $
 */
package datascript;
import java.util.Vector;
import java.util.Iterator;
import java.util.Stack;
import java.math.BigInteger;
import datascript.syntaxtree.Node;
import datascript.syntaxtree.NodeToken;
import datascript.syntaxtree.SqlSyntax;
import datascript.parser.DSConstants;

public class Field {
    private TypeInterface type;
    private String name;		// null is anonymous
    private CompoundType compound;	// compound type containing this field
    private Node fieldCondition;
    private Node fieldInitializer;
    private Node fieldLabel;
    private Node fieldGlobalLabel;
    private Vector sqlCondition = new Vector();
    private Node sqlFieldContent;
    private String sqlBlobCompressionStr;
    private String sqlBlobEncodingStr;
    private Node fieldOptionalClause; // ** from dstools

    private boolean isResolved = false;
    private boolean bitFieldFlag;	// flag to cache whether it's a bitfield
    int bitFieldOffset;			// offset in bitfield chain
    int totalBitFieldLength;		// total length of bitfield chain
					// only valid if this == bitFieldStart
    Field /* !? */ bitFieldStart;	// first field in bitfield chain

    private static int anonFieldCounter = 0;
    Field(TypeInterface type, String name, Node fieldLabel, Node fieldGlobalLabel) {
	this.type = type;
	if (name != null) {
	    this.name = name;
	} else {
	    this.name = "anonfield_" + anonFieldCounter++;
	}
	this.fieldLabel = fieldLabel;
	this.fieldGlobalLabel = fieldGlobalLabel;
    }

    IntegerValue sizeof(Context ctxt) {
    	 if (fieldOptionalClause != null)
         {
             throw new ComputeError("sizeof cannot be applied to optional field");
         }
	return type.sizeof(ctxt);
    }

    String getName() {
	return name;
    }

    TypeInterface getType() {
	return type;
    }

    Node getLabel() {
	return fieldLabel;
    }

    Node getGlobalLabel() {
	return fieldGlobalLabel;
    }

    CompoundType getCompound() {
	return compound;
    }

    boolean isBitField() {
	return bitFieldFlag;
    }

    void resolveFieldType() {
	type = TypeReference.resolveType(type);
	if (type instanceof ArrayType) {
	    ((ArrayType)type).resolveElementType(getCompound());
	}
	if (type instanceof TypeInstantiation) {
	    ((TypeInstantiation)type).resolveBaseType(getCompound());
	}

	// compute bitfieldflag
	TypeInterface ftype = type;
	if (ftype instanceof SetType) {
	    ftype = BuiltinType.getBuiltinType(ftype);
	}
	bitFieldFlag = ftype instanceof BitFieldType;
	isResolved   = true;
    }

    Node getCondition() {
	return fieldCondition;
    }
    
    Vector getSqlCondition() {
    	return sqlCondition;
    }
    
    Node getSqlFieldContent() {
    	return sqlFieldContent;
    }
    
    public Node getOptionalClause()
    {
        return fieldOptionalClause;
    }

    void setOptionalClause(Node optional)
    {
        this.fieldOptionalClause = optional;
    }

    void setCondition(Node fieldCondition) {
	this.fieldCondition = fieldCondition;
    }
    
    void setSqlCondition(Vector sqlConditionElements) {
    	
    	String[] departed_str;
    	String left_str;
    	String right_str;
    	
    	
    	for(int i = 0 ; i < sqlConditionElements.size() ; i++ ){
    		
//    		String debug_str = ((SqlSyntax)sqlConditionElements.elementAt(i)).toString();
//    		Node debug_node = ((Node)sqlConditionElements.elementAt(i));
    		
    		if( ((SqlSyntax)sqlConditionElements.elementAt(i)).nodeToken.toString().contains("=") ){
    			departed_str = ((SqlSyntax)sqlConditionElements.elementAt(i)).nodeToken.toString().replaceAll("\"","").split("=",2);
    			left_str     =        departed_str[0].trim();
    			right_str    =        departed_str[1].trim();
    			if(left_str.matches("blob_compression")  )	sqlBlobCompressionStr =  right_str.trim();
    			if(left_str.matches("blob_encoding")     ) 	sqlBlobEncodingStr 	  =	 right_str.trim();
    			
    			
    		}else sqlCondition.add(sqlConditionElements.elementAt(i)); 
    		
    	}
    }    
    

    void setSqlFieldContent(Node sqlFieldContent) {
		this.sqlFieldContent = sqlFieldContent;
	}

    Node getInitializer() {
	return fieldInitializer;
    }

    void setInitializer(Node fieldInitializer) {
	this.fieldInitializer = fieldInitializer;
    }
    
    boolean hasInitializer(){
    	return fieldInitializer != null; 
    }

    void setCompound(CompoundType compound) {
	this.compound = compound;
    }

    public String toString() {
	return "Field name='" + name + "' type='" + type + "' compound='" + compound + "'";
    }

    /**
     * A lookahead for a field.
     */
    public static class LookAhead {
	private int offset;		// typically zero for direct lookahead
	private int lkind;		// lookahead kind (as in DSConstants.*)
	private RangeMap rangemap;	// key range determines valid values

	int getOffset() { return offset; }
	int getKind() { return lkind; }
	RangeMap getRangeMap() { return rangemap; }

	public LookAhead(RangeMap rangemap, int lkind, int offset) {
	    this.rangemap = rangemap;
	    this.lkind = lkind;
	    this.offset = offset;
	}

	public LookAhead symDifference(LookAhead that) {
	    if (this.offset != that.offset)
		return null;
	    if (this.lkind != that.lkind)
		return null;
	    RangeMap symDiff = this.rangemap.symDifference(that.rangemap);
	    if (symDiff == null)
		return null;
	    return new LookAhead(symDiff, this.lkind, this.offset);
	}
	// public String toString() { return "LookAhead[" + rangemap + ", " + lkind + "@" + offset + "]"; }
	public String toString() { return ToString.print(this); }
    }

    /**
     * Does this field have a lookahead.
     * @return lookahead, or null if not
     */
    LookAhead getLookAhead() {
	if (!isResolved)
	    throw new InternalError("please resolve field type first");

	if (fieldLabel != null || fieldGlobalLabel != null)	// don't support lookahead with labels yet
	    return null;
	
	 // cannot compute lookahead for optional fields
    if (fieldOptionalClause != null) // ** from dstools
        return null;

	if (type instanceof TypeReference || type instanceof TypeInstantiation || type instanceof BitFieldType) {
	    return null;	// not supported
	}

	if (type instanceof ArrayType) {
	    TypeInterface etype = ((ArrayType)type).getElementType();
	    if (etype instanceof CompoundType) {
		LookAhead la = ((CompoundType)etype).getLookAhead();
		la.getRangeMap().setAllValues(this);
		return la;
	    }
	    return null;
	} else
	if (type instanceof CompoundType) {
	    LookAhead la = ((CompoundType)type).getLookAhead();
	    if (la != null)
		la.getRangeMap().setAllValues(this);
	    return la;
	}
	/* else it would be a field with a builtin-type */
	if (!(type instanceof BuiltinType))
	    return null;

	/* See if this condition allows us to straightforwardly deduce this
	 * field's range.  We make the following simplifying assumptions:
         * The condition only contains integer constants, comparison
	 * operators, boolean operators, and references to the field
	 * itself.   If any of these assumptions is violated, we simply
	 * give up and say we can't find a lookahead for this field.
	 *
	 * In the most general case, we'd have solve inequalities.  Later.
	 */
	final Context ctxt =  compound.getScope();
	int size = sizeof(ctxt).integerValue().intValue();
	int offset = 0;

	// only handle <= 32bit now
	if (size > 4)
	    return null;

	// initializer, treat like EQ
	Node init = getInitializer();
	if (init != null) {
	    try {
		ExpressionEvaluator ite = new ExpressionEvaluator(compound);
		Main.debug("finding lookahead for initializer " + Main.printNode(init));
		init.accept(ite);
		IntegerValue val = (IntegerValue)ite.value();
		if (!type.isMember(ctxt, val))
		    throw new ComputeError("initializer out of range");	// hmmm, might be redundant/already checked

		BigInteger bi = val.integerValue();
		checkBigInt(bi);
		return new LookAhead(new RangeMap(new RangeMap.Range(bi, bi.add(BigInteger.ONE)), this), ((BuiltinType)type).getKind(), offset);
	    } catch (ComputeError ce) {
		Main.debug("no lookahead from initializer found: " + ce);
		return null;
	    }
	}

	Node cond = getCondition();
	if (cond == null)
	    return null;

	ExpressionEmitterHelper em = new ExpressionEmitterHelper();
	ExpressionTypeCheckVisitor jvis = new ExpressionTypeCheckVisitor(compound, ctxt, em);
	try {
	    Main.debug("finding lookahead for " + Main.printNode(cond));
	    cond.accept(jvis);
	    return new LookAhead(em.getRangeMap(), ((BuiltinType)type).getKind(), offset);
	} catch (ComputeError ce) {
	    Main.debug("no lookahead found: " + ce);
	}
	return null;
    }

    private boolean isSigned() {
	if (!(type instanceof BuiltinType)) {
	    throw new ComputeError("isSigned: not a builtin type: " + type);
	}
	BuiltinType btype = (BuiltinType)type;
	return btype.isSigned();
    }

    private void checkBigInt(BigInteger bi) {
	if (bi.compareTo(new BigInteger(Integer.toString(Integer.MAX_VALUE))) > -1)
	    throw new ComputeError("integer too big " + bi); 
	if (bi.compareTo(new BigInteger(Integer.toString(Integer.MIN_VALUE))) == -1)
	    throw new ComputeError("integer too small " + bi); 
    }
    

    class ExpressionEmitterHelper implements ExpressionEmitter {
	private Stack rangeStack = new Stack();		// this stack can contain values, field name and ranges
	private Object currentObj;

	public RangeMap getRangeMap() { 
	    if (!(currentObj instanceof RangeMap))
		throw new ComputeError("not a rangemap " + currentObj);
	    return (RangeMap)currentObj;
	}

	public void push() { 
	    rangeStack.push(currentObj); 
	}

	public void value(Value value) { 
	    currentObj = value;
	}

	public void unaryOperation(NodeToken n) { 
	    throw new ComputeError("unary op " + n + " not implemented"); 
	}

	private void checkForRanges(Object left, Object right) {
	    if (!(left instanceof RangeMap))
		throw new ComputeError(left + " is not a rangemap");
	    if (!(right instanceof RangeMap))
		throw new ComputeError(right + " is not a rangemap");
	}

	private int flip(int kind) {
	    switch (kind) {
	    case DSConstants.EQ:
	    case DSConstants.NE:
		return kind;
	    case DSConstants.GT:
		return DSConstants.LE;
	    case DSConstants.GE:
		return DSConstants.LT;
	    case DSConstants.LT:
		return DSConstants.GE;
	    case DSConstants.LE:
		return DSConstants.GT;
	    }
	    throw new InternalError("bad flip");
	}

	private void doBinaryCmp(Field field, Value value, int kind) {
	    // as in left < right
	    BigInteger bi = value.integerValue();
	    checkBigInt(bi);
	    BuiltinType ftype = (BuiltinType)field.getType();

	    RangeMap r;
	    switch (kind) {
	    case DSConstants.EQ:
		r = new RangeMap(new RangeMap.Range(bi, bi.add(BigInteger.ONE)), field);
		break;

	    case DSConstants.NE:
		r = new RangeMap(new RangeMap.Range(bi.add(BigInteger.ONE), ftype.getUpperBound()), field);
		r.put(new RangeMap.Range(ftype.getLowerBound(), bi), field);
		break;

	    case DSConstants.GT:
		r = new RangeMap(new RangeMap.Range(bi.add(BigInteger.ONE), ftype.getUpperBound()), field);
		break;

	    case DSConstants.GE:
		r = new RangeMap(new RangeMap.Range(bi, ftype.getUpperBound()), field);
		break;

	    case DSConstants.LT:
		r = new RangeMap(new RangeMap.Range(ftype.getLowerBound(), bi), field);
		break;

	    case DSConstants.LE:
		r = new RangeMap(new RangeMap.Range(ftype.getLowerBound(), bi.add(BigInteger.ONE)), field);
		break;

	    default:
		throw new InternalError("bad binary cmp");
	    }
	    currentObj = r;
	}

	public void binaryOperation(NodeToken n) { 
	    Object left = rangeStack.pop();
	    Object right = currentObj;

	    switch (n.kind) {
	    case DSConstants.EQ:
	    case DSConstants.GT:
	    case DSConstants.GE:
	    case DSConstants.LT:
	    case DSConstants.LE:
	    case DSConstants.NE:
		if (left instanceof Field && right instanceof Value)
		    doBinaryCmp((Field)left, (Value)right, n.kind);
		else
		if (left instanceof Value && right instanceof Field)
		    doBinaryCmp((Field)right, (Value)left, flip(n.kind));
		else
		    throw new ComputeError(n + " not implemented for " + left + " with " + right);
		break;

	    case DSConstants.LOGICALAND:
		checkForRanges(left, right);
		RangeMap lrange = (RangeMap)left;
		RangeMap rrange = (RangeMap)right;
		currentObj = lrange.intersect(rrange);
		if (currentObj == null)
		    throw new ComputeError("&& emptied out");
		break;

	    case DSConstants.LOGICALOR:
		checkForRanges(left, right);
		currentObj = ((RangeMap)left).union((RangeMap)right);
		break;

	    default:
		throw new ComputeError("binary op " + n + " not implemented"); 
	    }
	}
	

	
	

	public void fieldAccess(Field field) { 
	    if (field != Field.this)  throw new ComputeError("no field access other than current field allowed"); 
	    currentObj = field;
	}
    public void lengthof()
    {
        throw new ComputeError("lengthof not supported");
    }
	public void ternaryOperation(NodeToken n0, NodeToken n1) { throw new ComputeError("ternary not supported"); }
	public void arrayAccess(ArrayType atype, TypeInterface etype) { throw new ComputeError("array access not supported"); }
	public void sizeof() { throw new ComputeError("sizeof not supported"); }
	public void localParameter(Parameter param) { throw new ComputeError("local parameter not supported"); }
	public void choiceCheck(UnionType utype, Field choice) { throw new ComputeError("choice check not supported"); }
	public void compoundReference(CompoundType ctype) { throw new ComputeError("compound reference not supported"); }
	public void qualifiedFieldAccess(Field field) { throw new ComputeError("qualified field access not supported"); }
	public void openConditionCall(Condition cond) { throw new ComputeError("open cond call not supported"); }
	public void openQualifiedConditionCall(Condition cond) { throw new ComputeError("open qualified cond call not supported"); }
	public void closeConditionCall(Condition cond, int nargs) { throw new ComputeError("close cond call not supported"); }
	public void castTo(TypeInterface srctype, TypeInterface dsttype) { throw new ComputeError(
	"cast to not supported"); }
	public void forall(Node expr, ArrayType atype) { throw new ComputeError("forall not supported"); }
	public void parenthesize() { throw new ComputeError("parenthesize not supported"); }
    }

	public String getSqlBlobCompressionStr() {
		return sqlBlobCompressionStr;
	}

	public String getSqlBlobEncodingStr() {
		return sqlBlobEncodingStr;
	}


}
