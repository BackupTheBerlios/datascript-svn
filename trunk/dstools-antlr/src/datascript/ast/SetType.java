/*
 * SetType.java
 *
 * @author: Godmar Back
 * @version: $Id: SetType.java,v 1.1.1.1 2003/05/29 22:58:25 gback Exp $
 */
package datascript.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import antlr.collections.AST;

public class SetType extends TokenAST implements TypeInterface
{

    protected HashMap items = new HashMap();

    protected TypeInterface type;

    protected String name;

    CompoundType ctype; // compound in which this set type is defined

    public SetType()
    {
        
    }
    public String getName()
    {
        if (name == null)
        {
            AST n = getFirstChild().getNextSibling();
            name = n.getText();
        }
        return name;
    }

    TypeInterface getBaseType()
    {
        return (TypeInterface)getFirstChild();
    }
    


    Value[] getValues()
    {
        Iterator it = items.values().iterator();
        Value[] values = new Value[items.size()];
        for (int i = 0; i < values.length; i++)
        {
            values[i] = (Value) it.next();
        }
        Arrays.sort(values);
        return values;
    }

    /**
     * not clear what this means - finding out whether it's a valid combination
     * of non-disjoint bitmask entries takes some work
     */
    public boolean isMember(Context ctxt, Value val)
    {
        Iterator i = items.keySet().iterator();
        while (i.hasNext())
        {
            if (val.compareTo(i.next()) == 0)
            {
                return true;
            }
        }
        return false;
    }

    public IntegerValue sizeof(Context ctxt)
    {
        return type.sizeof(ctxt);
    }

    public Value castFrom(Value val)
    {
        throw new InternalError("SetType.castFrom() not implemented");
    }
    
    public Scope getScope()
    {
        return null;
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
