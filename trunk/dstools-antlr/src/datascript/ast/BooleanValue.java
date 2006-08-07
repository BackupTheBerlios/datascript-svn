/*
 * BooleanValue.java
 *
 * @author: Godmar Back
 * @version: $Id: BooleanValue.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

public class BooleanValue extends Value implements Comparable
{
    private boolean bval;

    public boolean booleanValue()
    {
        return bval;
    }

    public BooleanValue(boolean bval)
    {
        this.bval = bval;
    }

    // boolean can implement compareTo only partially
    public int compareTo(Object o)
    {
        BooleanValue obval = (BooleanValue) o;
        return bval == obval.bval ? 0 : -1; /* ??? makes != same as < */
    }

    public String toString()
    {
        return "\"" + bval + "\"";
    }
    
    public BooleanValue not()
    {
        return new BooleanValue(!bval);
    }

    public TypeInterface getType()
    {
        return BooleanType.booleanType;
    }
}
