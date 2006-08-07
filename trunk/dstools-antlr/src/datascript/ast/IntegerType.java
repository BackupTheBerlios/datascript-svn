/*
 * IntegerType.java
 *
 * @author: Godmar Back
 * @version: $Id: IntegerType.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public class IntegerType extends TokenAST implements TypeInterface
{
    static IntegerType integerType = new IntegerType();

    public IntegerValue sizeof(Context ctxt)
    {
        throw new ComputeError(
                "not computing generic sizeof of unknown integer");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        throw new InternalError("IntegerType.isMember not implemented");
    }

    public Value castFrom(Value val)
    {
        throw new InternalError("IntegerType.castFrom not implemented");
    }

    public String toString()
    {
        return "integer";
    }

    public Scope getScope()
    {
        throw new InternalError("not implemented");
    }
    
    public static boolean checkCompatibility(TypeInterface type1, TypeInterface type2)
    {
        if (type1 instanceof IntegerType && (type2 instanceof IntegerType))
        {
            return true;
        }
        else 
            return type1.equals(type2);
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
