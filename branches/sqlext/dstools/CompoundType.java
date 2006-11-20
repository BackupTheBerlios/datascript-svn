/*
 * CompoundType.java
 *
 * @author: Godmar Back ( revisied by Duksu Han)
 * @version: $Id: CompoundType.java,v 1.2 2003/06/19 19:53:34 gback Exp $, 2006/13/10 Duksu Han
 */
package datascript;
import java.util.Vector;
import java.util.Stack;
import java.util.Iterator;

abstract public class CompoundType implements TypeInterface {
	Vector		fields = new Vector();
	Vector		nestedTypes = new Vector();
	Vector		conditions = new Vector();
	
	/// enum and bitmasks lexically contained in this compound
	Vector		settypes = new Vector();	
	Vector		parameters = null;
	
	/// set of compound types that can contain this type
	Vector		containers = new Vector();
	Vector 		index_fields = new Vector();
	Vector      constraint_sql_fields = new Vector();

	
	
	//? Index, Constraint_sql
	
	
	/// one of TypeInterface.NOBYTEORDER, BIGENDIAN, LITTLEENDIAN
	int 			byteOrder;	
	private Scope	scope;
	private String 	name;
	private CompoundType	parent;
	
	private boolean isAnonymous = false;
	private static int anonTypeCounter = 0;
	
	abstract public IntegerValue sizeof(Context ctxt);
	abstract public boolean isMember(Context ctxt, Value val);
	protected boolean bfoComputed = false;
	
	abstract void computeBitFieldOffsets(int offset);
	
	CompoundType(String name, CompoundType parent) {
		this.parent = parent;
		if (name != null) {
			this.name = name;
		} else {
			isAnonymous = true;
			this.name = "anontype_" + anonTypeCounter++;
		}
	}
	
	CompoundType getParent() {
		return parent;
	}
	
	boolean isAnonymous() {
		return isAnonymous;
	}
	
	void addParameters(Vector parameters) {
		this.parameters = parameters;
	}
	
	Iterator getParameters() {
		return parameters.iterator();
	}
	
	boolean hasParameters() {
		return parameters != null;
	}
	
	boolean isEmpty() {
		return fields.size() == 0 && conditions.size() == 0 && nestedTypes.size() == 0;
	}
	
	String getName() {
		return name;
	}
	
	String getName_with() {
		return name + "_v";
	}
	
	void addContainer(CompoundType f) {
		if (!containers.contains(f)) {
			if (Main.containedgraph != null) {
				Main.containedgraph.println("\t" 
						+ f.getName() + " -> " + getName() + ";");
			}
			Main.debug(f + " is a container of " + this);
			containers.addElement(f);
		} 
	}
	
	void resolveFieldTypes() {
		for (int i = 0; i < fields.size(); i++) {
			((Field)fields.elementAt(i)).resolveFieldType();
		}
		if (hasParameters()) {
			for (int i = 0; i < parameters.size(); i++) {
				((Parameter)parameters.elementAt(i)).resolveType(this);
			}
		}
		
		for (int i = 0; i < nestedTypes.size(); i++) {
			((CompoundType)nestedTypes.elementAt(i)).resolveFieldTypes();
		}
	}
	
	/**
	 * @return true if 'this' is contained in compound type 'f'
	 */
	boolean isContained(CompoundType f) {
		return isContained(f, new Stack());
	}
	
	/**
	 * The "is contained" relationship may contain cycles
	 * use a stack to avoid them.  
	 * This is a simple DFS path finding algorithm that finds
	 * a path from 'this' to 'f'.
	 */
	boolean isContained(CompoundType f, Stack seen) {
		Main.debug("is " + this + " contained in " + f);
		if (containers.contains(f)) {
			return true;
		}
		
		/* check whether any container of 'this' is contained in 'f' */
		for (int i = 0; i < containers.size(); i++) {
			CompoundType c = (CompoundType)containers.elementAt(i);
			if (seen.search(c) == -1) {
				seen.push(c);
				if (c.isContained(f, seen)) {
					return true;
				}
				seen.pop();
			}
		}
		return false;
	}
	
	Iterator getFields() {
		return fields.iterator();
	}
	
	boolean hasFields() {
		return fields != null;
	}
	
	
	void addField(Field f) {
		Main.assertThat(!fields.contains(f));
		String test = f.toString();
		
		if( f.getType() instanceof IndexType ){
			index_fields.addElement(f);
		}else if( f.getType() instanceof ConstraintSqlType ){
			constraint_sql_fields.addElement(f);
		}else{
			fields.addElement(f);
		}
		
	}
	
	
	Field getField(int i) {
		return (Field)fields.elementAt(i);
	}
	
	Iterator getSetTypes() {
		return settypes.iterator();
	}
	
	void addSetType(SetType stype) {
		Main.assertThat(!settypes.contains(stype));
		settypes.addElement(stype);
	}
	
	Iterator getNestedTypes() {
		return nestedTypes.iterator();
	}
	
	void addNestedType(CompoundType c) {
		nestedTypes.addElement(c);
	}
	
	Iterator getConditions() {
		return conditions.iterator();
	}
	
	// ** from dstools
	  Iterable<TypeInterface> getNestedTypes_()
	    {
	        return nestedTypes;
	    }
	 
	void addCondition(Condition c) {
		conditions.addElement(c);
	}
	
	int getByteOrder() {
		return byteOrder; 
	}
	
	void setByteOrder(int byteOrder) {
		this.byteOrder = byteOrder;
	}
	
	public Value castFrom(Value val) {
		throw new CastError("casting compounds not implemented");
	}
	
	Scope getScope() {
		return scope;
	}
	
	void setScope(Scope scope) {
		this.scope = scope;
	}
	
	void link() {
		scope.link(null);
	}
	
	public String toString() {
		return name;
	}
	
	public abstract Field.LookAhead getLookAhead();
	
	
	Iterator getConstraint_sql_fields() {
		return constraint_sql_fields.iterator();
	}
	Iterator getIndex_fields() {
		return index_fields.iterator();
	}
	
	boolean hasIndex_fields() {
		return index_fields != null;
	}
	
	boolean hasConstraint_sql_fields() {
		return !constraint_sql_fields.isEmpty();
	}
}
