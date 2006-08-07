/*
 * TypeInterface.java
 *
 * @author: Godmar Back
 * @version: $Id: TypeInterface.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public interface TypeInterface {
    final static int NO_BYTE_ORDER = 0;
    final static int BIG_ENDIAN = 1;
    final static int LITTLE_ENDIAN = 2;
    final static int DEFAULT_BYTE_ORDER = BIG_ENDIAN;

    /**
     * Return size of of this type, if known.
     *
     * @throws ComputeError if not known
     */
    IntegerValue sizeof(Context ctxt);

    /**
     * Is this val a member of this type in this context?
     */
    boolean isMember(Context ctxt, Value val);

    /**
     * Return a value of this type, cast from another value
     */
    Value castFrom(Value val);
    public Scope getScope();
    public int getLength();
    public Expression getLengthExpression();
    
}
