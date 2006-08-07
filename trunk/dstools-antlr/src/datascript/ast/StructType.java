/*
 * StructType.java
 *
 * @author: Godmar Back
 * @version: $Id: StructType.java,v 1.2 2003/06/19 19:53:34 gback Exp $
 */
package datascript.ast;


public class StructType extends CompoundType
{

    public StructType()
    {        
    }
    
    public IntegerValue sizeof(Context ctxt)
    {
        IntegerValue size = new IntegerValue(0);
        IntegerValue eight = new IntegerValue(8);

        for (int i = 0; i < fields.size(); i++)
        {
            Field fi = (Field) fields.elementAt(i);
            /* TODO:
            try
            {
                StdIntegerType b = StdIntegerType.getBuiltinType(fi.getFieldType());
                if (b instanceof BitFieldType)
                {
                    size = size.add(new IntegerValue(((BitFieldType) b)
                            .getLength()));
                    continue;
                }
            }
            catch (ClassCastException _)
            {
            }
                */
            size = size.add(fi.sizeof(ctxt).multiply(eight));
        }
        return size.divide(eight);
    }

    public boolean isMember(Context ctxt, Value val)
    {
        // do something like
        // if val.getType() == this
        throw new ComputeError("isMember not implemented");
    }


}
