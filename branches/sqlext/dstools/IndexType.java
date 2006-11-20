package datascript;

/*
 * IndexType.java
 *
 * @author: duksu Han
 * @version: $Id: IndexType.java,v 1.5 2006/08/31 
 */

public class IndexType implements TypeInterface {
    static IndexType IndexType = new IndexType();

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
       return "index";
    }
    
    
}