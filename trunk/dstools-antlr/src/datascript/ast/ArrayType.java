/*
 * ArrayType.java
 *
 * @author: Godmar Back
 * @version: $Id: ArrayType.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;


public class ArrayType extends TokenAST implements TypeInterface
{
    private Scope scope;
    private int length;
    private Expression lengthExpr;

    public ArrayType()
    {
    }

    public int getLength()
    {
        Value value = getLengthExpression().getValue();
        length = (value == null) ? 0 : value.integerValue().intValue();
        return length;
    }
    
    public Expression getLengthExpression()
    {
        if (lengthExpr == null)
        {
            lengthExpr = (Expression) getFirstChild().getNextSibling();
        }
        return lengthExpr;
    }
    
    public boolean isVariable()
    {
        return getLength() != 0;
    }


    public IntegerValue sizeof(Context ctxt)
    {
        throw new ComputeError("sizeof array not known");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        throw new ComputeError("ArrayType.isMember not implemented");
    }

    public Value castFrom(Value val)
    {
        throw new ComputeError("cannot cast " + val + " into " + this);
    }

    public TypeInterface getElementType()
    {        
        TypeInterface type =(TypeInterface) getFirstChild();
        return TypeReference.resolveType(type);
    }

    public String toString()
    {
        return "array of " + getElementType();
    }

    public Scope getScope()
    {
        return scope;
    }
    
}
