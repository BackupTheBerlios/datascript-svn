/*
 * IntegerValue.java
 *
 * @author: Godmar Back
 * @version: $Id: IntegerValue.java,v 1.2 2003/06/19 19:53:34 gback Exp $
 */
package datascript.ast;

import java.math.BigInteger;

public class IntegerValue extends Value implements Comparable
{
    private BigInteger ival;

    public IntegerValue(String ival)
    {
        this.ival = new BigInteger(ival);
    }

    public IntegerValue(BigInteger ival)
    {
        this.ival = ival;
    }

    public IntegerValue(int ival)
    {
        this.ival = new BigInteger(new Integer(ival).toString());
    }

    public BigInteger integerValue()
    {
        return (ival);
    }

    public IntegerValue add(IntegerValue v2)
    {
        return new IntegerValue(ival.add(v2.ival));
    }

    public IntegerValue subtract(IntegerValue v2)
    {
        return new IntegerValue(ival.subtract(v2.ival));
    }

    public IntegerValue multiply(IntegerValue v2)
    {
        return new IntegerValue(ival.multiply(v2.ival));
    }

    public IntegerValue divide(IntegerValue v2)
    {
        return new IntegerValue(ival.divide(v2.ival));
    }

    public IntegerValue remainder(IntegerValue v2)
    {
        return new IntegerValue(ival.remainder(v2.ival));
    }

    public IntegerValue and(IntegerValue v2)
    {
        return new IntegerValue(ival.and(v2.ival));
    }

    public IntegerValue or(IntegerValue v2)
    {
        return new IntegerValue(ival.or(v2.ival));
    }

    public IntegerValue xor(IntegerValue v2)
    {
        return new IntegerValue(ival.xor(v2.ival));
    }

    public IntegerValue shiftLeft(IntegerValue v2)
    {
        return new IntegerValue(ival.shiftLeft(v2.ival.intValue()));
    }

    public IntegerValue shiftRight(IntegerValue v2)
    {
        return new IntegerValue(ival.shiftRight(v2.ival.intValue()));
    }

    public IntegerValue not()
    {
        return new IntegerValue(ival.not());
    }

    public IntegerValue negate()
    {
        return new IntegerValue(ival.negate());
    }

    public int compareTo(Object obj)
    {
        IntegerValue ivalobject = (IntegerValue) obj;
        return this.ival.compareTo(ivalobject.ival);
    }

    public String toString()
    {
        return ival.toString() + "L";
    }

    public TypeInterface getType()
    {
        return IntegerType.integerType;
    }
}
