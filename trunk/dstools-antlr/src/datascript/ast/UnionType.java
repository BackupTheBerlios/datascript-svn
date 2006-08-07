/*
 * UnionType.java
 *
 * @author: Godmar Back
 * @version: $Id: UnionType.java,v 1.2 2003/06/19 19:53:34 gback Exp $
 */
package datascript.ast;

public class UnionType extends CompoundType
{

    public UnionType()
    {

    }

    public IntegerValue sizeof(Context ctxt)
    {
        IntegerValue size = ((Field) fields.elementAt(0)).sizeof(ctxt);
        for (int i = 1; i < fields.size(); i++)
        {
            if (size.compareTo(((Field) fields.elementAt(i)).sizeof(ctxt)) != 0)
            {
                throw new ComputeError("sizeof is unknown");
            }
        }
        return size;
    }

    public boolean isMember(Context ctxt, Value val)
    {
        // do something like
        // if val.getType() == this
        throw new ComputeError("isMember not implemented");
    }

}
