/*
 * Value.java
 *
 * @author: Godmar Back
 * @version: $Id: Value.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import java.math.BigInteger;

public abstract class Value implements Comparable
{

    public static Value makeValue(String sval)
    {
        // DS.debug("creating Value with " + sval);
        //
        if (sval.startsWith("\""))
        {
            return null; // TODO: new StringValue(sval.substring(1, sval.length() - 1));
        }
        else if (sval.startsWith("0x"))
        {
            return new IntegerValue(new BigInteger(sval.substring(2), 16));
        }
        else if (sval.endsWith("b") || sval.endsWith("B"))
        {
            return new IntegerValue(new BigInteger(sval.substring(0, sval
                    .length() - 1), 2));
        }
        else if (sval.startsWith("0"))
        {
            return new IntegerValue(new BigInteger(sval, 8));
        }
        else if ("123456789".indexOf(sval.charAt(0)) != -1)
        {
            return new IntegerValue(new BigInteger(sval, 10));
        }
        else if (sval.equals("true"))
        {
            return new BooleanValue(true);
        }
        else if (sval.equals("false"))
        {
            return new BooleanValue(false);
        }
        else
            throw new ComputeError("inconsistent format: " + sval);
    }

    public String stringValue()
    {
        throw new ComputeError("not a string value: " + this);
    }

    public boolean booleanValue()
    {
        throw new ComputeError("not a boolean value: " + this);
    }

    public BigInteger integerValue()
    {
        throw new ComputeError("not an integer value: " + this);
    }

    abstract public int compareTo(Object obj);

    abstract public TypeInterface getType();
}
