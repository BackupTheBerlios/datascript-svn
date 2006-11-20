package datascript;

/*
 * IndexType.java
 *
 * @author: duksu Han
 * @version: $Id: IndexType.java,v 1.5 2006/08/31 
 */

import datascript.parser.DSConstants;
import java.math.BigInteger;

public class ConstraintSqlType implements TypeInterface {
    static ConstraintSqlType constraintSQLType = new ConstraintSqlType();

    public IntegerValue sizeof(Context ctxt) {
       throw new ComputeError("cannot apply sizeof to string type");
    }

    public boolean isMember(Context ctxt, Value val) {
       return (val instanceof StringValue);
    }

    public Value castFrom(Value val) {
       // throw new CastError("casting to string not implemented");
       return new StringValue(val.toString());	// !?
    }

    public String toString() {
       return "constraint";
    }
    
    
}