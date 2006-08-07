/*
 * BooleanType.java
 *
 * @author: Godmar Back
 * @version: $Id: BooleanType.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public class BooleanType extends TokenAST implements TypeInterface
{
    static BooleanType booleanType = new BooleanType();

    public IntegerValue sizeof(Context ctxt)
    {
        throw new ComputeError("cannot apply sizeof to boolean type");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        return (val instanceof BooleanValue);
    }

    public Value castFrom(Value val)
    {
        throw new InternalError("not implemented");
    }

    public String toString()
    {
        return "boolean";
    }

    public Scope getScope()
    {
        throw new InternalError("not implemented");
    }

    public int getLength()
    {
        throw new InternalError("not implemented");
    }
    
    public Expression getLengthExpression()
    {
        throw new InternalError("not implemented");
    }
    
    
}
