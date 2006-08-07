/*
 * BitFieldType.java
 *
 * @author: Godmar Back
 * @version: $Id: BitFieldType.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import java.math.BigInteger;

public class BitFieldType extends IntegerType
{
    /** 
     * Number of bits of this bitfield. 
     * When length is known at compile time, this attribute is set to a value
     * > 0. Otherwise, length is set to -1, and the runtime length is
     * indicated by the expression stored in lengthExpr. 
     */
    int length;

    /**
     * Expression indicating the run-time length of this bitfield.
     * If this is null, length must be set to a value > 0. Otherwise,
     * length == -1.
     */
    Expression lengthExpr;

    BigInteger lowerBound, upperBound;

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
            lengthExpr = (Expression) getFirstChild();
        }
        return lengthExpr;
    }    
    
    public boolean isVariable()
    {
        return getLength() != 0;
    }

    public BitFieldType()
    {
    }

    public IntegerValue sizeof(Context ctxt)
    {
        /** @TODO handle variable length! */
        if (length % 8 == 0)
            return new IntegerValue(length / 8);
        throw new ComputeError(
                "bitfield length not an integral number of bytes");
    }

    public boolean isMember(Context ctxt, Value val)
    {
        /** @TODO handle variable length! */
        try
        {
            return (lowerBound.compareTo(val.integerValue()) != 1 && val
                    .integerValue().compareTo(upperBound) == -1);
        }
        catch (ComputeError _)
        {
            return (false);
        }
    }

    public String toString()
    {
        return "BitField"; 
        // We no longer append "/* :" + length + " */", since this causes
        // a nested comment with an array of bitfield.
    }
}
